package ru.itlab.reviewservice.storage.dto;

import lombok.Builder;

@Builder
public record MenuRatingInfo(
    Long menuId,
    Float wilsonScore,
    Float avgStars
) {

}
