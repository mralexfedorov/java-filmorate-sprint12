package ru.yandex.prakticum.filmorate.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.prakticum.filmorate.model.Film;
import ru.yandex.prakticum.filmorate.service.FilmService;
import ru.yandex.prakticum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Component
public class FilmController {
    @NonNull
    private InMemoryFilmStorage inMemoryFilmStorage;
    @NonNull
    private FilmService filmService;

    @GetMapping("/films")
    public Collection<Film> getAllFilms() {
        return inMemoryFilmStorage.getAll();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable int id) {
        return inMemoryFilmStorage.getById(id);
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        inMemoryFilmStorage.add(film);
        return film;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        inMemoryFilmStorage.update(film);
        return film;
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public Collection<Film> getMostPopularFilms(@RequestParam(value = "count", defaultValue = "10", required = false)
                                                    int count){

        return filmService.getMostPopularFilms(count);
    }
}
