package ru.itlab.reviewservice.storage.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import ru.itlab.reviewservice.storage.util.DateUtil;

import java.time.LocalDateTime;
import java.util.Objects;

import static ru.itlab.reviewservice.storage.util.HibernateUtil.getEffectiveClass;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "menu_id", nullable = false)
    private Long menuId;
    @Column(name = "created_by", nullable = false)
    private String createdBy;
    @Column(name = "comment")
    private String comment;
    @Column(name = "rate", nullable = false)
    private Integer rate;
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    @CreationTimestamp
    @DateTimeFormat(pattern = DateUtil.DATE_FORMAT)
    private LocalDateTime createdAt;

    @Override
    public final boolean equals(Object o)  {
        if (this == o) return true;
        if (o == null || getEffectiveClass(this) != getEffectiveClass(o)) return false;
        Review review = (Review) o;
        return getId() != null && Objects.equals(getId(), review.getId());
    }

    @Override
    public final int hashCode() {
        return getEffectiveClass(this).hashCode();
    }
}
