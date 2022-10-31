package ru.yandex.prakticum.filmorate.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.prakticum.filmorate.model.Film;
import ru.yandex.prakticum.filmorate.model.Genre;
import ru.yandex.prakticum.filmorate.model.Mpa;
import ru.yandex.prakticum.filmorate.storage.*;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    @NonNull
    @Qualifier("filmDbStorage")
    private FilmStorage filmStorage;
    @NonNull
    @Qualifier("userDbStorage")
    private UserStorage userStorage;
    @NonNull
    private FilmLikesDBStorage filmLikesDBStorage;
    @NonNull
    private GenreDBStorage genreDBStorage;
    @NonNull
    private MpaDBStorage mpaDBStorage;

    public void addLike(int id, int userId) {
        filmStorage.checkFilmNotFound(id);
        userStorage.checkUserNotFound(userId);

        filmLikesDBStorage.add(id, userId);

        log.info("Пользовать с id: '{}' поставил лайк фильму c id: '{}'",userId , id);
    }

    public void deleteLike(int id, int userId) {
        filmStorage.checkFilmNotFound(id);
        userStorage.checkUserNotFound(userId);

        filmLikesDBStorage.delete(id, userId);

        log.info("Пользовать с id: '{}' удалил лайк фильму c id: '{}'",userId , id);
    }

    public Collection<Film> getMostPopularFilms(int count) {
        return filmStorage.getMostPopularFilms(count);
    }

    public Collection<Genre> getGenres() {
        return genreDBStorage.getAll();
    }

    public Genre getGenreById(int id) {
        return genreDBStorage.getById(id);
    }

    public Collection<Mpa> getMpa() {
        return mpaDBStorage.getAll();
    }

    public Mpa getMpaById(int id) {
        return mpaDBStorage.getById(id);
    }
}
