package ru.itlab.reviewservice.storage.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

import static ru.itlab.reviewservice.storage.util.HibernateUtil.getEffectiveClass;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "ratings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"menu_id"}))
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "menu_id", nullable = false)
    private Long menuId;
    @Column(name = "rate_one", nullable = false)
    private Integer rateOne;
    @Column(name = "rate_two", nullable = false)
    private Integer rateTwo;
    @Column(name = "rate_three", nullable = false)
    private Integer rateThree;
    @Column(name = "rate_four", nullable = false)
    private Integer rateFour;
    @Column(name = "rate_five", nullable = false)
    private Integer rateFive;
    @Column(name = "wilson_score", nullable = false)
    private Float wilsonScore;
    @Column(name = "avg_stars", nullable = false)
    private Float avgStars;

    public static Rating newRating(Long menuId) {
        return newRating(menuId, 0, 0, 0, 0, 0);
    }

    public static Rating newRating(Long menuId, int one, int two, int three, int four, int five) {
        return Rating.builder()
                .id(null)
                .menuId(menuId)
                .rateOne(one)
                .rateTwo(two)
                .rateThree(three)
                .rateFour(four)
                .rateFive(five)
                .wilsonScore(0.0f)
                .avgStars(0.0f)
                .build();
    }

    @Override
    public final boolean equals(Object o)  {
        if (this == o) return true;
        if (o == null || getEffectiveClass(this) != getEffectiveClass(o)) return false;
        Rating rating = (Rating) o;
        return getId() != null && Objects.equals(getId(), rating.getId());
    }

    @Override
    public final int hashCode() {
        return getEffectiveClass(this).hashCode();
    }
}
