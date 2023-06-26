package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeDBStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.Validator;

import java.util.List;


@Slf4j
@Service
public class FilmServiceImpl implements FilmService {
    private final FilmStorage fs;
    private final UserStorage us;
    private final LikeDBStorage ls;

    @Autowired
    public FilmServiceImpl(@Qualifier("filmDBStorage") FilmStorage fs, @Qualifier("userDBStorage") UserStorage us,
            LikeDBStorage ls) {
        this.fs = fs;
        this.us = us;
        this.ls = ls;
    }

    @Override
    public FilmDTO saveFilm(FilmDTO filmDTO) {
        Film film = FilmMapper.dtoToFilm(filmDTO);
        if (Validator.filmValidator(film)) {
            log.debug("Film {} сохранён.", filmDTO.getId());
            return FilmMapper.filmToDTO(fs.saveFilm(film));
        }
        throw new ValidationException("Валидация Film " + filmDTO + " не пройдена");
    }

    @Override
    public FilmDTO updateFilm(FilmDTO filmDTO) {
        Film film = FilmMapper.dtoToFilm(filmDTO);
        if (Validator.filmValidator(film)) {
            log.debug("Film c ID {} обновлён.", filmDTO.getId());
            return FilmMapper.filmToDTO(fs.updateFilm(film));
        }
        throw new ValidationException("Валидация Film " + filmDTO + " не пройдена");
    }

    @Override
    public List<FilmDTO> readAllFilms() {
        log.debug("Полный список Films возвращён.");
        return FilmMapper.listFilmsToListDto(fs.readAllFilms());
    }

    @Override
    public FilmDTO getFilmByID(Long id) {
        log.debug("Film c ID {} возвращён.", id);
        return FilmMapper.filmToDTO(fs.getFilmById(id));
    }

    @Override
    public void deleteLikeById(Long idFilm, Long idUser) {
        fs.getFilmById(idFilm);
        us.getUserById(idUser); // для валидации
        ls.deleteLike(idFilm, idUser);
        log.debug("Лайк User c ID {} удалён у Film c ID {}", idUser, idFilm);
    }

    @Override
    public void userLike(Long idFilm, Long idUser) {
        fs.getFilmById(idFilm);
        us.getUserById(idUser); // для валидации
        ls.addLike(idFilm, idUser);
        log.debug("Лайк у User c ID {} установлен Film c ID {}.", idUser, idFilm);
    }

    @Override
    public List<FilmDTO> readTopFilms(Long count) {
        log.debug("Получен список из {} Film по кол-ву Likes.", count);
        return FilmMapper.listFilmsToListDto(fs.getTopFilms(count));
    }
}
