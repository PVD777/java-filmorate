package ru.yandex.practicum.filmorate.storage.review;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Review> reviewRowMapper = (resultSet, rowNum) -> {
        Review review = new Review(
                resultSet.getInt("review_id"),
                resultSet.getString("content"),
                resultSet.getBoolean("is_positive"),
                resultSet.getInt("user_id"),
                resultSet.getInt("film_id")
        );
        return review;
    };

    @Override
    public Review addReview(Review review) {
        if (!isUserExists(review.getUserId())) {
            log.error("Попытка добавления отзыва от несуществующего пользователя");
            throw new UserNotFoundException("Пользватель не существует");
        }
        if (!isFilmExists(review.getFilmId())) {
            log.error("Попытка добавления отзыва на несуществующий фильм");
            throw new UserNotFoundException("Фильм не существует");
        }
        Map<String,Object> keys = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("REVIEWS")
                .usingColumns("content", "is_positive", "user_id", "film_id")
                .usingGeneratedKeyColumns("review_id")
                .executeAndReturnKeyHolder(Map.of("content", review.getContent(),
                        "is_positive", review.getIsPositive(),
                        "user_id", review.getUserId(),
                        "film_id", review.getFilmId()))
                .getKeys();
        review.setReviewId((Integer) keys.get("review_id"));
        log.info("Добавлен новый отзыв id = {}", review.getReviewId());
        return review;
    }

    @Override
    public Optional<Review> getReview(int reviewId) {
        if (!isReviewExists(reviewId)) {
            log.error("Попытка получения несуществующего отзыва id = {}", reviewId);
            return Optional.empty();
        }
        Review review = jdbcTemplate.queryForObject("SELECT * FROM REVIEWS WHERE REVIEW_id = ?",
                reviewRowMapper, reviewId);
        log.info("Найден отзывы id = {}", review.getReviewId());
        return Optional.of(review);
    }

    @Override
    public void removeReview(int reviewId) {
        if (!isReviewExists(reviewId)) {
            log.info("Отзыв с id = {} отсутствует", reviewId);
            throw new ReviewNotFoundException("Указанное ревью не существует");
        }
        String sql = "DELETE FROM REVIEWS_LIKES WHERE review_id = ?";
        jdbcTemplate.update(sql, reviewId);
        sql = "DELETE FROM REVIEWS WHERE review_id = ?";
        jdbcTemplate.update(sql, reviewId);
        log.info("Отзыв id = {} удален", reviewId);
    }

    @Override
    public Optional<Review> updateReview(Review review) {
        if (!isReviewExists(review.getReviewId())) {
            log.info("Отзыв с id = {} отсутствует", review.getReviewId());
            return Optional.empty();
        }
        String sql = "UPDATE REVIEWS SET CONTENT = ?, IS_POSITIVE = ? WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sql,review.getContent(), review.getIsPositive(), review.getReviewId());
        log.info("Отзыв id = {} успешно обновлен", review.getReviewId());
        return getReview(review.getReviewId());
    }

    @Override
    public List<Review> getListOfReviews(Integer filmId, Integer count) {
            String sql = "SELECT * FROM REVIEWS";
            if (filmId != null) {
                sql = sql + " WHERE film_id = " + filmId;
            }
            if (count != null) {
                sql = sql + " LIMIT " + count;
            }
        return jdbcTemplate.query(sql, reviewRowMapper);
    }


    private boolean isFilmExists(int id) {
        String sql = "SELECT count(*) FROM FILMS WHERE film_id = ?";
        int count = jdbcTemplate.queryForObject(sql, new Object[] { id }, Integer.class);
        return count > 0;
    }

    private boolean isUserExists(int id) {
        String sql = "SELECT count(*) FROM USERS WHERE user_id = ?";
        int count = jdbcTemplate.queryForObject(sql, new Object[] { id }, Integer.class);
        return count > 0;
    }

    private boolean isReviewExists(int id) {
        String sql = "SELECT count(*) FROM REVIEWS WHERE review_id = ?";
        int count = jdbcTemplate.queryForObject(sql, new Object[] { id }, Integer.class);
        return count > 0;
    }


}
