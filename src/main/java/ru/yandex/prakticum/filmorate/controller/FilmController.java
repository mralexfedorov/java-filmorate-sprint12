package ru.yandex.prakticum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.prakticum.filmorate.exception.ValidationException;
import ru.yandex.prakticum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@Slf4j
public class FilmController {
    private final HashMap<String, Film> films = new HashMap<>();

    @GetMapping("/films")
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        String description = film.getDescription();
        if (description.length() > 200) {
            log.info("Описание фильма '{}' не может быть более 200 символов", description);
            throw new ValidationException("Описание фильма не может быть более 200 символов.");
        }

        LocalDate releaseDate = film.getReleaseDate();
        if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Дата релиза фильма '{}' не может быть ранее 28.12.1895г.", releaseDate);
            throw new ValidationException("Дата релиза фильма не может быть ранее 28.12.1895г.");
        }

        int duration = film.getDuration();
        if (duration < 0) {
            log.info("Продолжительность фильма '{}' не может быть меньше нуля.", duration);
            throw new ValidationException("Продолжительность фильма не может быть меньше нуля.");
        }

        String name = film.getName();
        if (films.get(name) == null) {
            films.put(name, film);
        } else {
            log.info("Фильм '{}' уже существует.", film);
            throw new ValidationException("Фильм уже существует.");
        }
        films.put(name, film);
        log.info("Добавлен фильм: '{}'", film.toString());

        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        String name = film.getName();

        if (films.get(name) != null) {
            films.replace(name, film);
        } else {
            log.info("Фильм '{}' не найден.", film);
            throw new ValidationException("Фильм не найден.");
        }
        log.info("Обновлен фильм: '{}'", film.toString());

        return film;
    }
}