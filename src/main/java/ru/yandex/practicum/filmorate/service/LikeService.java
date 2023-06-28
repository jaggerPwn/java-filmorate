package ru.yandex.practicum.filmorate.service;

import java.util.List;
import java.util.Set;

public interface LikeService {

    void deleteLike(Long filmId, Long userId);

    void addLike(Long filmId, Long userId);

    Set<Long> getLikerByFilmId(Long filmID);

    List<Long> getUsersWithSameLikes(Long userId);

    List<Long> getFilmRecommendationsFrom(Long userId, List<Long> sameUserIds);

}
