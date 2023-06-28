package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class LikeServiceImpl implements LikeService {

    private final LikeStorage likeStorage;

    @Autowired
    public LikeServiceImpl(LikeStorage likeStorage) {
        this.likeStorage = likeStorage;
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        likeStorage.deleteLike(filmId, userId);
    }

    @Override
    public void addLike(Long idFilm, Long idUser) {
        likeStorage.addLike(idFilm, idUser);
    }

    @Override
    public Set<Long> getLikerByFilmId(Long filmID) {
        return likeStorage.getLikerByFilmId(filmID);
    }

    @Override
    public List<Long> getUsersWithSameLikes(Long userId) {
        return likeStorage.getUsersWithSameLikes(userId);
    }

    @Override
    public List<Long> getFilmRecommendationsFrom(Long userId, List<Long> sameUserIds) {
        return likeStorage.getFilmRecommendationsFrom(userId,sameUserIds);
    }

}
