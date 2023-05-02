package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventTypes;
import ru.yandex.practicum.filmorate.model.OperationTypes;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.reviewsLikes.ReviewLikesStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final ReviewLikesStorage reviewLikesStorage;
    private final UserService userService;

    public Review addReview(Review review) {
        Review addedReview = reviewStorage.addReview(review);
        Event event = new Event(
                addedReview.getUserId(),
                EventTypes.REVIEW,
                OperationTypes.ADD,
                addedReview.getReviewId()
        );
        userService.addUserEvent(event);

        return addedReview;
    }

    public void removeReview(int id) {
        Review review = getReview(id);
        int userId = review.getUserId();
        reviewStorage.removeReview(id);
        Event event = new Event(
                userId,
                EventTypes.REVIEW,
                OperationTypes.REMOVE,
                id
        );
        userService.addUserEvent(event);
    }

    public Review getReview(int id) {
        Review review = reviewStorage.getReview(id).orElseThrow(() ->
                new ReviewNotFoundException("Запрошенный отзыв не найден"));
        review.setUseful(reviewLikesStorage.getReviewRating(id));
        return review;
    }

    public Review updateReview(Review review) {
        Review updatedReview = reviewStorage.updateReview(review).orElseThrow(() -> new ReviewNotFoundException("Запрошенный отзыв не найден"));
        Event event = new Event(
                updatedReview.getUserId(),
                EventTypes.REVIEW,
                OperationTypes.UPDATE,
                updatedReview.getReviewId()
        );
        userService.addUserEvent(event);

        return updatedReview;
    }

    public List<Review> getListOfReviews(Integer filmId, Integer count) {
        List<Review> listOfReview = reviewStorage.getListOfReviews(filmId, count);
        for (Review review : listOfReview) {
            review.setUseful(reviewLikesStorage.getReviewRating(review.getReviewId()));
        }

        return listOfReview.stream()
                .sorted(Comparator.comparing(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }

    public Review putLikeToReview(int reviewId, int userId) {
        Review review = getReview(reviewId);
        reviewLikesStorage.setLikeToReview(userId, reviewId);
        review.setUseful(reviewLikesStorage.getReviewRating(reviewId));
        return review;
    }

    public Review putdislikeToReview(int reviewId, int userId) {
        Review review = getReview(reviewId);
        reviewLikesStorage.setDislikeToReview(userId, reviewId);
        review.setUseful(reviewLikesStorage.getReviewRating(reviewId));
        return review;
    }

    public Review deleteLikeFromReview(int reviewId, int userId) {
        Review review = getReview(reviewId);
        reviewLikesStorage.deleteLikeFromReview(userId, reviewId);
        review.setUseful(reviewLikesStorage.getReviewRating(reviewId));
        return review;
    }

    public Review deletedislikeFromReview(int reviewId, int userId) {
        Review review = getReview(reviewId);
        reviewLikesStorage.deleteDislikeFromReview(userId, reviewId);
        review.setUseful(reviewLikesStorage.getReviewRating(reviewId));
        return review;
    }


}
