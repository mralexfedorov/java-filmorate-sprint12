package ru.yandex.prakticum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.prakticum.filmorate.exception.ObjectAlreadyExistException;
import ru.yandex.prakticum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.prakticum.filmorate.exception.ValidationException;
import ru.yandex.prakticum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage{
    private HashMap<Integer, Film> films = new HashMap<>();
    private int idByDefault = 1;

    @Override
    public Film getById(int id) {
        checkFilmNotFound(id);
        return films.get(id);
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public void add(Film film) {
        int id = film.getId();

        if (id != 0) {
            checkFilmIdNotNegative(id);
            checkFilmAlreadyExist(id);
        }

        checkFilmReleaseDate(film.getReleaseDate());
        checkFilmDuration(film.getDuration());
        checkFilmDescription(film.getDescription());

        if (id == 0) {
            id = setIdByDefault();
            film.setId(id);
        }

        film.setLikes(new HashSet<>());
        films.put(id, film);
        log.info("Добавлен фильм: '{}'", film.toString());
    }

    @Override
    public void update(Film film) {
        int id = film.getId();

        checkFilmIdNotNull(id);
        checkFilmIdNotNegative(id);
        checkFilmNotFound(id);

        checkFilmReleaseDate(film.getReleaseDate());
        checkFilmDuration(film.getDuration());
        checkFilmDescription(film.getDescription());

        Film updatedFilm = getById(id);
        film.setLikes(updatedFilm.getLikes());
        films.replace(id, film);
        log.info("Обновлен фильм: '{}'", film.toString());
    }

    private void checkFilmAlreadyExist(int id) {
        if (films.containsKey(id)) {
            log.info("Фильм с id '{}' уже существует.", id);
            throw new ObjectAlreadyExistException(String.format(
                    "Фильм с id %s уже существует.",
                    id
            ));
        }
    }

    public void checkFilmNotFound(int id) {
        if (!films.containsKey(id)) {
            log.info("Фильм с id '{}' не найден.", id);
            throw new ObjectNotFoundException(String.format(
                    "Фильм с id %s не найден.",
                    id
            ));
        }
    }

    private void checkFilmIdNotNull(int id) {
        if (id == 0) {
            log.info("id '{}' не заполнен.", id);
            throw new ValidationException("id не заполнен.");
        }
    }

    private void checkFilmIdNotNegative(int id) {
        if (id < 0) {
            log.info("id '{}' не может быть отрицательным.", id);
            throw new ValidationException("id не может быть отрицательным.");
        }
    }

    private void checkFilmReleaseDate(LocalDate releaseDate) {
        if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Дата релиза фильма '{}' не может быть ранее 28.12.1895г.", releaseDate);
            throw new ValidationException("Дата релиза фильма не может быть ранее 28.12.1895г.");
        }
    }

    private void checkFilmDuration(int duration) {
        if (duration < 0) {
            log.info("Продолжительность фильма '{}' не может быть меньше нуля.", duration);
            throw new ValidationException("Продолжительность фильма не может быть меньше нуля.");
        }
    }

    private void checkFilmDescription(String description) {
        if (description.length() > 200) {
            log.info("Описание фильма '{}' не может быть более 200 символов", description);
            throw new ValidationException("Описание фильма не может быть более 200 символов.");
        }
    }

    private int setIdByDefault() {
        return idByDefault++;
    }
}
