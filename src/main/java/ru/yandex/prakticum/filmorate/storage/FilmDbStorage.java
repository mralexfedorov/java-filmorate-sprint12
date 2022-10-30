package ru.yandex.prakticum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.prakticum.filmorate.exception.ObjectAlreadyExistException;
import ru.yandex.prakticum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.prakticum.filmorate.exception.ValidationException;
import ru.yandex.prakticum.filmorate.model.Film;
import ru.yandex.prakticum.filmorate.model.Genre;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Component("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage{
    private final JdbcTemplate jdbcTemplate;
    private final GenreDBStorage genreDBStorage;
    private final MpaDBStorage mpaDBStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDBStorage genreDBStorage, MpaDBStorage mpaDBStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDBStorage = genreDBStorage;
        this.mpaDBStorage = mpaDBStorage;
    }

    @Override
    public Film getById(int id) {
        String sqlQuery = "select name, description, release_date, duration, rate, mpa_id " +
                "from films where film_id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if(filmRows.next()) {
            Film film = Film.builder()
                    .id(id)
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .releaseDate(filmRows.getDate("release_date").toLocalDate())
                    .duration(filmRows.getInt("duration"))
                    .rate(filmRows.getInt("rate"))
                    .mpa(mpaDBStorage.getById(filmRows.getInt("mpa_id")))
                    .build();

            Set<Genre> filmGenres= genreDBStorage.getFilmGenres(id);

            for (Genre genre: filmGenres) {
                film.addGenre(genre);
            }

            return film;
        } else {
            log.info("Фильм с id '{}' не найден.", id);
            throw new ObjectNotFoundException(String.format(
                    "Фильм с id %s не найден.",
                    id
            ));
        }
    }

    @Override
    public Collection<Film> getAll() {
        final String sqlQuery = "select * from films";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRowToFilm(rs, rowNum));
    }

    @Override
    public void add(Film film) {
        final String sqlQuery = "insert into films (name, description, release_date, duration, rate, mpa_id) " +
                "values (?, ?, ?, ?, ?, ?)";

        int id = film.getId();

        if (id != 0) {
            checkFilmIdNotNegative(id);
            checkFilmAlreadyExist(id);
        }

        checkFilmReleaseDate(film.getReleaseDate());
        checkFilmDuration(film.getDuration());
        checkFilmDescription(film.getDescription());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getRate());
            stmt.setInt(6, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        if (id == 0) {
            id = Objects.requireNonNull(keyHolder.getKey()).intValue();
            film.setId(id);
        }

        Set<Genre> filmGenres = film.getGenres();
        if (!filmGenres.isEmpty()) {
            genreDBStorage.addFilmGenres(filmGenres, id);
        }

        log.info("Добавлен фильм: '{}'", film.toString());
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "update films set " +
                "name = ?, description = ?, release_date = ?, duration = ?, rate = ?, mpa_id = ? " +
                "where film_id = ?";

        int id = film.getId();

        checkFilmIdNotNull(id);
        checkFilmIdNotNegative(id);
        checkFilmNotFound(id);
        checkFilmReleaseDate(film.getReleaseDate());
        checkFilmDuration(film.getDuration());
        checkFilmDescription(film.getDescription());

        jdbcTemplate.update(sqlQuery
                ,film.getName()
                ,film.getDescription()
                ,film.getReleaseDate()
                ,film.getDuration()
                ,film.getRate()
                ,film.getMpa().getId()
                ,id);

        Set<Genre> filmGenres = film.getGenres();
        genreDBStorage.deleteFilmGenres(id);
        if (!filmGenres.isEmpty()) {
            genreDBStorage.addFilmGenres(filmGenres, id);
        }

        return getById(id);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .rate(rs.getInt("rate"))
                .mpa(mpaDBStorage.getById(rs.getInt("mpa_id")))
                .build();

        Set<Genre> filmGenres= genreDBStorage.getFilmGenres(film.getId());

        for (Genre genre: filmGenres) {
            film.addGenre(genre);
        }

        return film;
    }

    @Override
    public void checkFilmNotFound(int id) {
        if (getById(id) == null) {
            log.info("Фильм с id '{}' не найден.", id);
            throw new ObjectNotFoundException(String.format(
                    "Фильм с id %s не найден.",
                    id
            ));
        }
    }

    private void checkFilmAlreadyExist(int id) {
        if (getById(id) != null) {
            log.info("Фильм с id '{}' уже существует.", id);
            throw new ObjectAlreadyExistException(String.format(
                    "Фильм с id %s уже существует.",
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
}
