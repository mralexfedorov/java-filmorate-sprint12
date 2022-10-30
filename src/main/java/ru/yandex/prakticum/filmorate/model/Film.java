package ru.yandex.prakticum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import org.apache.logging.log4j.util.PropertySource;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
public class Film {
    @NotNull
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    private int duration;
    @NotNull
    private int rate;
    @NotNull
    private Mpa mpa;
    @NotNull
    private final Set<Genre> genres = new TreeSet<>(Comparator.comparingInt(Genre::getId));

    private Set<Integer> likes;

    public void setLikes(Set<Integer> likes) {
        this.likes = likes;
    }

    public void addLike(int id) {
        likes.add(id);
    }

    public void deleteLike(int id) {
        likes.remove(id);
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }
}
