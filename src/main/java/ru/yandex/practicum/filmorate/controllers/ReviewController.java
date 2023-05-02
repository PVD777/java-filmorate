package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;


import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping()
    public Review addReview(@RequestBody @Valid Review review) {
        return reviewService.addReview(review);
    }

    @DeleteMapping("/{reviewId}")
    public void deleteReview(@PathVariable int reviewId) {
        reviewService.removeReview(reviewId);
    }

    @GetMapping("/{reviewId}")
    public Review getReview(@PathVariable int reviewId) {
       return reviewService.getReview(reviewId);
    }

    @PutMapping
    public Review updateReview(@RequestBody @Valid Review review) {
        return reviewService.updateReview(review);
    }

    @GetMapping
    public List<Review> getListOfReviews(@RequestParam(required = false) Integer filmId,
                                         @RequestParam(required = false) Integer count) {
        return reviewService.getListOfReviews(filmId, count);
    }

    @PutMapping("{reviewId}/like/{userId}")
    public Review putLikeToReview(@PathVariable int reviewId, @PathVariable int userId) {
        return reviewService.putLikeToReview(reviewId,userId);
    }

    @PutMapping("{reviewId}/dislike/{userId}")
    public Review putdislikeToReview(@PathVariable int reviewId, @PathVariable int userId) {
        return reviewService.putdislikeToReview(reviewId,userId);
    }

    @DeleteMapping("{reviewId}/like/{userId}")
    public Review deleteLikeFromReview(@PathVariable int reviewId, @PathVariable int userId) {
        return reviewService.deleteLikeFromReview(reviewId, userId);
    }

    @DeleteMapping("{reviewId}/dislike/{userId}")
    public Review deletedislikeFromReview(@PathVariable int reviewId, @PathVariable int userId) {
        return reviewService.deletedislikeFromReview(reviewId, userId);
    }
}
