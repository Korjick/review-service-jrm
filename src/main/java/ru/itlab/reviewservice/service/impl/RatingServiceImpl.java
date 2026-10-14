package ru.itlab.reviewservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itlab.reviewservice.dto.GetRatingsRequest;
import ru.itlab.reviewservice.dto.RatingsResponse;
import ru.itlab.reviewservice.service.RatingService;
import ru.itlab.reviewservice.storage.dto.MenuRatingInfo;
import ru.itlab.reviewservice.storage.repositories.RatingRepository;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository repository;

    @Transactional
    @Override
    public void saveRating(Long menuId, Integer rate) {
        ensureRatingExists(menuId);
        repository.incrementRating(menuId, rate);
    }


    @Override
    public MenuRatingInfo getRatingOfMenu(Long menuId) {
        return repository.findRatingInfoByMenuId(menuId)
                .orElse(defaultRating(menuId));
    }

    @Override
    public RatingsResponse getRatingsOfMenus(GetRatingsRequest request) {
        var menuIdToRatings = repository
                .findRatingInfosByMenuIdIn(request.menuIds())
                .stream()
                .collect(Collectors.toMap(MenuRatingInfo::menuId, Function.identity()));

        List<MenuRatingInfo> result = request.menuIds().stream()
                .map(id -> menuIdToRatings.getOrDefault(id, defaultRating(id)))
                .collect(Collectors.toList());

        return RatingsResponse.builder()
                .menuRatings(result)
                .build();
    }

    private void ensureRatingExists(Long menuId) {
        repository.insertNoConflict(menuId);
    }

    private MenuRatingInfo defaultRating(Long menuId) {
        return MenuRatingInfo.builder()
                .menuId(menuId)
                .wilsonScore(0.0f)
                .avgStars(0.0f)
                .build();
    }
}