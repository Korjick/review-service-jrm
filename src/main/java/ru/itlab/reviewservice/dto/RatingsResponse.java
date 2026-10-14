package ru.itlab.reviewservice.dto;

import lombok.Builder;
import ru.itlab.reviewservice.storage.dto.MenuRatingInfo;

import java.util.List;

@Builder
public record RatingsResponse(
        List<MenuRatingInfo> menuRatings
) {
}
