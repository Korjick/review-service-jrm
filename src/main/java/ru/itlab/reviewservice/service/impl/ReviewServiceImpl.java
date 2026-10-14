package ru.itlab.reviewservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itlab.reviewservice.dto.CreateReviewRequest;
import ru.itlab.reviewservice.dto.RatedReviewsResponse;
import ru.itlab.reviewservice.dto.ReviewResponse;
import ru.itlab.reviewservice.dto.SortBy;
import ru.itlab.reviewservice.exception.ReviewServiceException;
import ru.itlab.reviewservice.mapper.ReviewMapper;
import ru.itlab.reviewservice.service.RatingService;
import ru.itlab.reviewservice.service.ReviewService;
import ru.itlab.reviewservice.storage.models.Review;
import ru.itlab.reviewservice.storage.repositories.ReviewRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final RatingService ratingService;

    @Transactional
    @Override
    public ReviewResponse createReview(CreateReviewRequest request, String username) {
        try {
            var review = reviewRepository.save(reviewMapper.toDomain(request, username));
            saveRating(request);
            return reviewMapper.toReviewResponse(review);
        } catch (DataIntegrityViolationException ex) {
            var msg =
                    "Failed to create Review to menu with id %d by user with name: %s, because the user already placed Review to that menu."
                            .formatted(request.menuId(), username);
            throw new ReviewServiceException(msg, HttpStatus.CONFLICT);
        }
    }

    @Override
    public ReviewResponse getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .map(reviewMapper::toReviewResponse)
                .orElseThrow(() -> {
                    var msg = "Review with id=%d not found.".formatted(reviewId);
                    return new ReviewServiceException(msg, HttpStatus.NOT_FOUND);
                });
    }

    @Override
    public List<ReviewResponse> getReviewsOfUser(String username, SortBy sort, int from, int size) {
        var pageable = getPageable(sort, from, size);
        List<Review> reviews = reviewRepository.findAllByCreatedBy(username, pageable);
        return reviewMapper.toReviewResponseList(reviews);
    }

    @Override
    public RatedReviewsResponse getRatedReviewsForMenu(Long menuId, SortBy sort, int from, int size) {
        var pageable = getPageable(sort, from, size);
        var reviews = reviewMapper
                .toReviewResponseList(reviewRepository.findAllByMenuId(menuId, pageable));
        var ratingInfo = ratingService.getRatingOfMenu(menuId);

        return RatedReviewsResponse.builder()
                .reviews(reviews)
                .menuRating(ratingInfo)
                .build();
    }

    private Pageable getPageable(SortBy sort, int from, int size) {
        return PageRequest.of(from, size)
                .withSort(sort.getSort());
    }

    private void saveRating(CreateReviewRequest request) {
        ratingService.saveRating(request.menuId(), request.rate());
    }
}