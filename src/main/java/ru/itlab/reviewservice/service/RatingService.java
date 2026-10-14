package ru.itlab.reviewservice.service;

import ru.itlab.reviewservice.dto.GetRatingsRequest;
import ru.itlab.reviewservice.dto.RatingsResponse;
import ru.itlab.reviewservice.storage.dto.MenuRatingInfo;

public interface RatingService {

    void saveRating(Long menuId, Integer rate);

    MenuRatingInfo getRatingOfMenu(Long menuId);

    RatingsResponse getRatingsOfMenus(GetRatingsRequest request);
}
