package ru.itlab.reviewservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.itlab.reviewservice.BaseIntegrationTest;
import ru.itlab.reviewservice.dto.GetRatingsRequest;
import ru.itlab.reviewservice.dto.RatedReviewsResponse;
import ru.itlab.reviewservice.dto.RatingsResponse;
import ru.itlab.reviewservice.dto.ReviewResponse;
import ru.itlab.reviewservice.storage.dto.MenuRatingInfo;


import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static ru.itlab.reviewservice.testutil.TestConstants.*;
import static ru.itlab.reviewservice.testutil.TestData.*;
import static ru.itlab.reviewservice.testutil.TestUtils.compareMenuInfo;

@AutoConfigureWebTestClient
class ReviewControllerTest extends BaseIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getRatingsOfMenus_returnsCorrectRatings_whenSomeMenusHaveReviews() {
        var menusWithReviews = Set.of(4L, 5L, 6L, 7L, 8L, 11L, 12L);
        var request = GetRatingsRequest.builder()
                .menuIds(menusWithReviews)
                .build();

        webTestClient.post()
                .uri(BASE_URL + "/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RatingsResponse.class)
                .value(response -> {
                    var menuRatingInfos = response.menuRatings();
                    menuRatingInfos.sort(Comparator.comparing(MenuRatingInfo::menuId));
                    assertThat(menuRatingInfos).hasSize(menusWithReviews.size());

                    var allRatingsHaveReviews = allRatingsHaveReviews();

                    for (int i = 0; i < menuRatingInfos.size(); i++) {
                        var rating = menuRatingInfos.get(i);
                        if (i < allRatingsHaveReviews.size()) {
                            compareMenuInfo(allRatingsHaveReviews.get(i), rating);
                        } else {
                            compareDefaultMenuInfo(rating.menuId(), rating);
                        }
                    }
                });
    }

    @Test
    void getRatingsOfMenus_returnsCorrectRatings_whenAllMenusHaveReviews() {
        var menusWithReviews = Set.of(4L, 5L, 6L, 7L, 8L);
        var request = GetRatingsRequest.builder()
                .menuIds(menusWithReviews)
                .build();

        webTestClient.post()
                .uri(BASE_URL + "/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RatingsResponse.class)
                .value(response -> {
                    var menuRatingInfos = response.menuRatings();
                    assertThat(menuRatingInfos).hasSize(menusWithReviews.size());
                    menuRatingInfos.sort(Comparator.comparing(MenuRatingInfo::menuId));
                    var allRatingsHaveReviews = allRatingsHaveReviews();
                    for (int i = 0; i < menuRatingInfos.size(); i++) {
                        compareMenuInfo(allRatingsHaveReviews.get(i), menuRatingInfos.get(i));
                    }
                });
    }

    @Test
    void getRatingsOfMenus_returnsDefaultRatingsWhenMenusHaveNoReviews() {
        var menusWithNoReviews = Set.of(1000L, 2000L, 3000L);
        var request = GetRatingsRequest.builder()
                .menuIds(menusWithNoReviews)
                .build();

        webTestClient.post()
                .uri(BASE_URL + "/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RatingsResponse.class)
                .value(response -> {
                    var menuRatingInfos = response.menuRatings();
                    assertThat(menuRatingInfos).hasSize(menusWithNoReviews.size());
                    for (var menuRatingInfo : menuRatingInfos) {
                        compareDefaultMenuInfo(menuRatingInfo.menuId(), menuRatingInfo);
                    }
                });
    }

    @Test
    void getReviewsOfMenu_returnsCorrectResponse_whenMenuHasReviews() {
        webTestClient.get()
                .uri(BASE_URL + "/menu-item/" + MENU_TEN + "?from=0&size=10&sortBy=date_asc")
                .exchange()
                .expectStatus().isOk()
                .expectBody(RatedReviewsResponse.class)
                .value(response -> {
                    assertThat(response.reviews())
                            .hasSize(5)
                            .isSortedAccordingTo(Comparator.comparing(ReviewResponse::createdAt));
                    compareMenuInfo(ratingMenuTen(), response.menuRating());
                });
    }

    @Test
    void getReviewsOfMenu_returnsEmptyListWithDefaultRating_whenMenuHasNoReviews() {
        long menuWithNoReviews = 1000;
        webTestClient.get()
                .uri(BASE_URL + "/menu-item/" + menuWithNoReviews + "?from=0&size=10&sortBy=date_asc")
                .exchange()
                .expectStatus().isOk()
                .expectBody(RatedReviewsResponse.class)
                .value(response -> {
                    assertThat(response.reviews()).isEmpty();
                    compareDefaultMenuInfo(menuWithNoReviews, response.menuRating());
                });

    }

    @Test
    void getReviewsOfUser_returnsCorrectList_whenUserHasReviews() {
        webTestClient.get()
                .uri(BASE_URL + "/my?" + "from=0&size=10&sortBy=date_asc")
                .header(ReviewController.USER_HEADER, USER_NAME)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ReviewResponse.class)
                .value(reviewResponses -> {
                    assertThat(reviewResponses)
                            .hasSize(5)
                            .isSortedAccordingTo(Comparator.comparing(ReviewResponse::createdAt));
                });
    }

    @Test
    void getReviewsOfUser_returnsEmptyList_whenUserHasNoReviews() {
        String userWithNoReviews = "Unknown user";
        webTestClient.get()
                .uri(BASE_URL + "/my?" + "from=0&size=10&sortBy=date_asc")
                .header(ReviewController.USER_HEADER, userWithNoReviews)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ReviewResponse.class)
                .value(reviewResponses -> {
                    assertThat(reviewResponses).isEmpty();
                });
    }

    @Test
    void getReview_returnsReview() {
        var reviewId = getReviewIdByMenuId(MENU_ONE);

        webTestClient.get()
                .uri(BASE_URL + "/" + reviewId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ReviewResponse.class)
                .value(response -> {
                    assertThat(response.id()).isNotNull();
                    assertThat(response.menuId()).isEqualTo(MENU_ONE);
                    assertThat(response.createdBy()).isEqualTo(USER_ONE);
                    assertThat(response.rate()).isEqualTo(RATE_FIVE);
                    assertThat(response.createdAt()).isEqualTo(REVIEW_DATE);
                    assertThat(response.comment()).isEqualTo(COMMENT_ONE);
                });
    }

    @Test
    void getReview_returnsNotFoundWhenNoReviewWithThatId() {
        long unknown = 1000L;
        webTestClient.get()
                .uri(BASE_URL + "/" + unknown)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void createReview_createsReview() {
        var request = createReviewRequest(MENU_ONE, 5);
        var username = "Alex";

        LocalDateTime now = LocalDateTime.now().minusNanos(1000);

        webTestClient.post()
                .uri(BASE_URL)
                .header(ReviewController.USER_HEADER, username)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ReviewResponse.class)
                .value(response -> {
                    assertThat(response.id()).isNotNull();
                    assertThat(response.menuId()).isEqualTo(request.menuId());
                    assertThat(response.createdBy()).isEqualTo(username);
                    assertThat(response.rate()).isEqualTo(request.rate());
                    assertThat(response.createdAt()).isAfter(now);
                });
    }

    @Test
    void createReview_returnsConflictWhenUserTriesToSendSecondReviewToSameMenu() {
        var request = createReviewRequest(MENU_ONE, 5);

        webTestClient.post()
                .uri(BASE_URL)
                .header(ReviewController.USER_HEADER, USER_ONE)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }
}
