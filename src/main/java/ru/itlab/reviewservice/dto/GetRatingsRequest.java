package ru.itlab.reviewservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.Set;

@Builder
public record GetRatingsRequest(
        @NotEmpty
        Set<Long> menuIds
) {
}
