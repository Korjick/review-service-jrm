package ru.itlab.reviewservice.dto;

import lombok.Builder;
import ru.itlab.reviewservice.storage.dto.MenuRatingInfo;

import java.util.List;

@Builder
public record RatedReviewsResponse(
        List<ReviewResponse> reviews,
        MenuRatingInfo menuRating
) {
}
