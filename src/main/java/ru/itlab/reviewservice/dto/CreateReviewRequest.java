package ru.itlab.reviewservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import ru.itlab.reviewservice.dto.validation.NullOrNotBlank;


@Builder
public record CreateReviewRequest(
        @Positive(message = "Идентификатор блюда должен быть > 0")
        Long menuId,
        @NullOrNotBlank(message = "Комментарий не должен быть пустым.")
        String comment,
        @Min(value = 1, message = "Рейтинг должен быть от 1 до 5.")
        @Max(value = 5, message = "Рейтинг должен быть от 1 до 5.")
        Integer rate
) {
}
