package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.ReviewDTO;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewMapper {

    public static ReviewDTO reviewToDTO(Review review) {
        if (review == null) {
            throw new IllegalArgumentException("review cannot be null");
        }

        return ReviewDTO.builder()
                .reviewId(review.getReviewId())
                .content(review.getContent())
                .filmId(review.getFilmId())
                .userId(review.getUserId())
                .isPositive(review.getIsPositive())
                .useful(review.getUseful())
                .build();
    }

    public static Review dtoToReview(ReviewDTO reviewDTO) {
        if (reviewDTO == null) {
            throw new IllegalArgumentException("reviewDTO cannot be null");
        }

        return Review.builder()
                .reviewId(reviewDTO.getReviewId())
                .content(reviewDTO.getContent())
                .filmId(reviewDTO.getFilmId())
                .userId(reviewDTO.getUserId())
                .isPositive(reviewDTO.getIsPositive())
                .useful(reviewDTO.getUseful())
                .build();
    }

    public static List<ReviewDTO> listUsersToListDto(Collection<Review> reviews) {
        return reviews.stream().map(ReviewMapper::reviewToDTO).collect(Collectors.toList());
    }

}
