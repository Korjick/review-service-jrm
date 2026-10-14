package ru.itlab.reviewservice.service;

import ru.itlab.reviewservice.dto.CreateReviewRequest;
import ru.itlab.reviewservice.dto.RatedReviewsResponse;
import ru.itlab.reviewservice.dto.ReviewResponse;
import ru.itlab.reviewservice.dto.SortBy;

import java.util.List;

public interface ReviewService {

    ReviewResponse createReview(CreateReviewRequest request, String username);

    ReviewResponse getReview(Long reviewId);

    List<ReviewResponse> getReviewsOfUser(String username, SortBy sort, int from, int size);

    RatedReviewsResponse getRatedReviewsForMenu(Long menuId, SortBy sort, int from, int size);
}
