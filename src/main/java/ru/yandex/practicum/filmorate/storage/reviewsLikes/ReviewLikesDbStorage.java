package ru.yandex.practicum.filmorate.storage.reviewsLikes;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class ReviewLikesDbStorage  implements ReviewLikesStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public int getReviewRating(int reviewId) {
        String sql = "SELECT ( " +
                "(SELECT COUNT (user_id) FROM REVIEWS_LIKES WHERE review_id = ? AND is_positive = true ) - " +
                "(SELECT COUNT (user_id) FROM REVIEWS_LIKES WHERE review_id = ? AND is_positive = false ))";
        int count = jdbcTemplate.queryForObject(sql, new Object[] { reviewId, reviewId}, Integer.class);
        return count;
    }

    @Override
    public void setLikeToReview(int userId, int reviewId) {
        if (checkForRate(userId, reviewId)) {
            log.info("От пользователя id = {} на отзыв Id = {} оценка уже есть", userId, reviewId);
            return;
        }
        new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("REVIEWS_LIKES")
                .usingColumns("user_id", "review_Id", "is_positive")
                .execute(Map.of("user_id", userId,
                        "review_Id", reviewId,
                        "is_positive", true));
        log.info("Пользователь Id = {} оценил отзыв id = {} положительно", userId, reviewId);
    }

    @Override
    public void setDislikeToReview(int userId, int reviewId) {
        if (checkForRate(userId, reviewId)) {
            log.info("От пользователя id = {} на отзыв Id = {} оценка уже есть", userId, reviewId);
            return;
        }
        new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("REVIEWS_LIKES")
                .usingColumns("user_id", "review_Id", "is_positive")
                .execute(Map.of("user_id", userId,
                        "review_Id", reviewId,
                        "is_positive", false));
        log.info("Пользователь Id = {} оценил отзыв id = {} отрицательно", userId, reviewId);
    }

    @Override
    public void deleteLikeFromReview(int userId, int reviewId) {
        if (!checkForRate(userId, reviewId)) {
            log.info("От пользователя id = {} на отзыв Id = {} нет оценки", userId, reviewId);
            return;
        }
        jdbcTemplate.update("DELETE FROM REVIEWS_LIKES WHERE user_id = ? AND reviewId = ?", userId, reviewId);
        log.info("От id = {} на отзыв Id = {} лайк удален", userId, reviewId);
    }

    @Override
    public void deleteDislikeFromReview(int userId, int reviewId) {
        if (!checkForRate(userId, reviewId)) {
            log.info("От пользователя id = {} на отзыв Id = {} нет оценки", userId, reviewId);
            return;
        }
        jdbcTemplate.update("DELETE FROM REVIEWS_LIKES WHERE user_id = ? AND reviewId = ?", userId, reviewId);
        log.info("От id = {} на отзыв Id = {} ДИЗлайк удален", userId, reviewId);
    }

    private boolean checkForRate(int userId, int reviewId) {
        String sql = "Select * FROM REVIEWS_LIKES WHERE user_id = ? AND review_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId, reviewId);
        return rowSet.next();



    }
}
