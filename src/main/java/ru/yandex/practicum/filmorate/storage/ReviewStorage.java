package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review saveReview(Review review);

    Review updateReview(Review review);

    List<Review> readAllReviews(Long filmId, Long count);

    Review getReviewById(Long id);

    void deleteReviewById(Long id);

    void saveReviewLikesOrDislikes(Long reviewId, Long userId, boolean positive);

    void deleteReviewLikesOrDislikes(Long reviewId, Long userId, boolean positive);
}
