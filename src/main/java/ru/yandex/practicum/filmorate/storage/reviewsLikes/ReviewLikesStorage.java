package ru.yandex.practicum.filmorate.storage.reviewsLikes;


public interface ReviewLikesStorage {

    int getReviewRating(int reviewId);

    void setLikeToReview(int userId, int reviewId);

    void setDislikeToReview(int userId, int reviewId);

    void deleteLikeFromReview(int userId, int reviewId);

    void deleteDislikeFromReview(int userId, int reviewId);



}
