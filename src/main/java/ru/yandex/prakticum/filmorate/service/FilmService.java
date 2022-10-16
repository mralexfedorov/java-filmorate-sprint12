package ru.yandex.prakticum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.prakticum.filmorate.model.Film;
import ru.yandex.prakticum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.prakticum.filmorate.storage.InMemoryUserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private InMemoryFilmStorage inMemoryFilmStorage;
    private InMemoryUserStorage inMemoryUserStorage;

    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public void addLike(int id, int userId) {
        inMemoryFilmStorage.checkFilmNotFound(id);
        inMemoryUserStorage.checkUserNotFound(userId);

        Film film = inMemoryFilmStorage.getById(id);
        film.addLike(userId);
        inMemoryFilmStorage.update(film);

        log.info("Пользовать с id: '{}' поставил лайк фильму c id: '{}'",userId , id);
    }

    public void deleteLike(int id, int userId) {
        inMemoryFilmStorage.checkFilmNotFound(id);
        inMemoryUserStorage.checkUserNotFound(userId);

        Film film = inMemoryFilmStorage.getById(id);
        film.deleteLike(userId);
        inMemoryFilmStorage.update(film);

        log.info("Пользовать с id: '{}' удалил лайк фильму c id: '{}'",userId , id);
    }

    public Collection<Film> getMostPopularFilms(int count) {
        Map<Film, Integer> sortedByLikesFilms = new HashMap<>();
        for (Film film: inMemoryFilmStorage.getAll()) {
            sortedByLikesFilms.put(film, film.getLikes().size());
        }

        return sortedByLikesFilms.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).
                limit(count).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
                        LinkedHashMap::new)).keySet();
    }
}
