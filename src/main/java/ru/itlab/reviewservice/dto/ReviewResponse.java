package ru.itlab.reviewservice.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReviewResponse(
        Long id,
        Long menuId,
        String createdBy,
        String comment,
        Integer rate,
        LocalDateTime createdAt
) {

}
