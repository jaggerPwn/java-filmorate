package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.ReviewDTO;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserService userService;
    private final FeedService feedService;

    @Autowired
    public ReviewServiceImpl(ReviewStorage reviewStorage, UserService userService, FeedService feedService) {
        this.reviewStorage = reviewStorage;
        this.userService = userService;
        this.feedService = feedService;
    }


    @Override
    public ReviewDTO addReview(ReviewDTO reviewDTO) {
        Review review = reviewStorage.saveReview(ReviewMapper.dtoToReview(reviewDTO));
        feedService.saveFeed(review.getUserId(),
                Instant.now().toEpochMilli(),
                EventType.REVIEW, Operation.ADD,
                review.getReviewId());
        log.debug("получен запрос на добавление review");
        return ReviewMapper.reviewToDTO(review);
    }

    @Override
    public ReviewDTO updateReview(ReviewDTO reviewDTO) {
        Review review = reviewStorage.updateReview(ReviewMapper.dtoToReview(reviewDTO));
        feedService.saveFeed(review.getUserId(),
                Instant.now().toEpochMilli(),
                EventType.REVIEW, Operation.UPDATE,
                review.getReviewId());
        log.debug("получен запрос на обновление review c ID c {}", reviewDTO.getReviewId());
        return ReviewMapper.reviewToDTO(review);
    }

    @Override
    public void deleteReviewById(Long reviewId) {
        Review review = reviewStorage.getReviewById(reviewId);
        feedService.saveFeed(review.getUserId(),
                Instant.now().toEpochMilli(),
                EventType.REVIEW, Operation.REMOVE,
                reviewId);
        log.debug("получен запрос на удаление review c ID c {}", reviewId);
        reviewStorage.deleteReviewById(reviewId);
    }

    @Override
    public void addReviewLikeOrDislike(Long reviewId, Long userId, boolean positive) {
        Review review = this.reviewStorage.getReviewById(reviewId);
        UserDTO user = userService.getUserById(userId);
        if (positive)
            log.debug("Получен запрос на добавление Like для review c Id {} от user c ID {}.", reviewId, userId);
        if (!positive)
            log.debug("Получен запрос на добавление disLike для review c Id {} от user c ID {}.", reviewId, userId);
        reviewStorage.saveReviewLikesOrDislikes(reviewId, userId, positive);
    }

    @Override
    public void deleteReviewLikeOrDislike(Long reviewId, Long userId, boolean positive) {
        Review review = this.reviewStorage.getReviewById(reviewId);
        UserDTO user = userService.getUserById(userId);
        if (positive)
            log.debug("Получен запрос на удаление Like для review c Id {} от user c ID {}.", reviewId, userId);
        if (!positive)
            log.debug("Получен запрос на удаление disLike для review c Id {} от user c ID {}.", reviewId, userId);
        reviewStorage.deleteReviewLikesOrDislikes(reviewId, userId, positive);
    }

    @Override
    public ReviewDTO getReviewById(long reviewId) {
        log.debug("получен запрос на обновление Review c ID c {}", reviewId);
        return ReviewMapper.reviewToDTO(reviewStorage.getReviewById(reviewId));
    }

    @Override
    public List<ReviewDTO> getAllReviews(Long filmId, Long count) {
        log.debug("получен запрос на получение Review по Film ID c {}", filmId);
        return ReviewMapper.listUsersToListDto(reviewStorage.readAllReviews(filmId, count));
    }
}
