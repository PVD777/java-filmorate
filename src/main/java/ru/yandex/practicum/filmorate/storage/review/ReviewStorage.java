package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;


public interface ReviewStorage {
    Review addReview(Review review);

    Optional<Review> getReview(int reviewId);

    void removeReview(int reviewId);

    Optional<Review> updateReview(Review review);

    List<Review> getListOfReviews(Integer filmId, Integer count);
}
