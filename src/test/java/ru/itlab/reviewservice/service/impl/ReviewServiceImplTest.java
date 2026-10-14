package ru.itlab.reviewservice.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.itlab.reviewservice.BaseIntegrationTest;
import ru.itlab.reviewservice.dto.RatedReviewsResponse;
import ru.itlab.reviewservice.dto.ReviewResponse;
import ru.itlab.reviewservice.dto.SortBy;
import ru.itlab.reviewservice.exception.ReviewServiceException;
import ru.itlab.reviewservice.service.ReviewService;
import ru.itlab.reviewservice.storage.models.Rating;
import ru.itlab.reviewservice.storage.repositories.RatingRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.itlab.reviewservice.testutil.TestConstants.*;
import static ru.itlab.reviewservice.testutil.TestData.createReviewRequest;
import static ru.itlab.reviewservice.testutil.TestData.ratingMenuTen;
import static ru.itlab.reviewservice.testutil.TestUtils.compareMenuInfo;

class ReviewServiceImplTest extends BaseIntegrationTest {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private RatingRepository ratingRepository;


    @Test
    void getRatedReviewsForMenu_returnsCorrectResponse_whenMenuHasReviews() {
        Long menuTen = MENU_TEN;
        Rating ratingMenuTen = ratingMenuTen();
        RatedReviewsResponse response = reviewService.getRatedReviewsForMenu(menuTen, SortBy.DATE_ASC, 0, 5);
        var reviews = response.reviews();

        assertThat(reviews)
                .map(ReviewResponse::rate)
                .isEqualTo(List.of(5, 4, 3, 2, 1));

        assertThat(reviews)
                .map(ReviewResponse::createdAt)
                .isEqualTo(List.of(REVIEW_DATE_MENU_4,
                        REVIEW_DATE_MENU_5,
                        REVIEW_DATE_MENU_6,
                        REVIEW_DATE_MENU_7,
                        REVIEW_DATE_MENU_8));

        compareMenuInfo(ratingMenuTen, response.menuRating());
    }

    @Test
    void getRatedReviewsForMenu_returnsEmptyListAndDefaultRating_whenMenuHasNoReviews() {
        Long noReviewsMenu = MENU_UNKNOWN;
        RatedReviewsResponse response = reviewService.getRatedReviewsForMenu(noReviewsMenu, SortBy.DATE_ASC, 0, 10);
        assertThat(response.reviews()).isEmpty();
        compareDefaultMenuInfo(noReviewsMenu, response.menuRating());
    }

    @Test
    void getReviewsOfUser_returnsCorrectList_whenUserHasReviews() {
        List<ReviewResponse> reviews = reviewService.getReviewsOfUser(USER_NAME, SortBy.DATE_ASC, 0, 3);
        assertThat(reviews).hasSize(3);

        assertThat(reviews)
                .map(ReviewResponse::rate)
                .isEqualTo(List.of(5, 4, 3));

        assertThat(reviews)
                .map(ReviewResponse::createdAt)
                .isEqualTo(List.of(REVIEW_DATE_MENU_4,
                        REVIEW_DATE_MENU_5,
                        REVIEW_DATE_MENU_6));
    }

    @Test
    void getReviewsOfUser_returnsEmptyList_whenUserHasNoReviews() {
        List<ReviewResponse> reviews = reviewService
                .getReviewsOfUser("Unknown", SortBy.DATE_ASC, 0, 10);
        assertThat(reviews).isEmpty();
    }

    @Test
    void getReview_throwsIfReviewIsNotPresent() {
        Long unknownId = 1000L;
        assertThrows(ReviewServiceException.class,
                () -> reviewService.getReview(unknownId));
    }

    @Test
    void getReview_returnsReviewIfReviewIsPresent() {
        var id = getReviewIdByMenuId(MENU_ONE);
        ReviewResponse response = reviewService.getReview(id);
        assertThat(response.rate()).isEqualTo(RATE_FIVE);
        assertThat(response.comment()).isEqualTo(COMMENT_ONE);
        assertThat(response.menuId()).isEqualTo(MENU_ONE);
        assertThat(response.createdBy()).isEqualTo(USER_ONE);
        assertThat(response.createdAt()).isEqualTo(REVIEW_DATE);
    }

    @Test
    void createReview_throwsWhenUserAlreadyPlacedReviewForMenu() {
        var request = createReviewRequest(MENU_ONE, 5);
        assertThrows(ReviewServiceException.class,
                () -> reviewService.createReview(request, "UserOne"));
    }

    @Test
    void createReview_createsReviewAddsRatingWhenMenuHadNoRating() {
        var request = createReviewRequest(1000L, 5);
        ReviewResponse response = reviewService.createReview(request, "Alex");
        assertThat(response.rate()).isEqualTo(request.rate());
        assertThat(response.menuId()).isEqualTo(request.menuId());
        assertThat(response.menuId()).isNotNull();
        assertThat(response.createdBy()).isEqualTo("Alex");
        assertThat(response.comment()).isEqualTo(request.comment());

        var rating = ratingRepository.findByMenuId(request.menuId()).get();
        assertThat(rating.getRateFive()).isEqualTo(1);
    }

    @Test
    void createReview_createsReviewIncreasesRatingWhenMenuAlreadyHasRating() {
        var request = createReviewRequest(MENU_ONE, 5);

        ReviewResponse response = reviewService.createReview(request, "Alex");
        assertThat(response.rate()).isEqualTo(request.rate());
        assertThat(response.menuId()).isEqualTo(request.menuId());
        assertThat(response.id()).isNotNull();
        assertThat(response.createdBy()).isEqualTo("Alex");
        assertThat(response.comment()).isEqualTo(request.comment());

        Rating rating = ratingRepository.findByMenuId(request.menuId()).get();
        assertThat(rating.getRateFive()).isEqualTo(2);
    }
}