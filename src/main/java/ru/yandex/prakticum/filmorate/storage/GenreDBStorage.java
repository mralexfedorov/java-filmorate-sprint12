package ru.yandex.prakticum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.prakticum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.prakticum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component()
@Slf4j
public class GenreDBStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre getById(int id) {
        String sqlQuery = "select name " +
                "from genres where id = ?";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if(genreRows.next()) {
            Genre genre = Genre.builder()
                    .id(id)
                    .name(genreRows.getString("name"))
                    .build();

            return genre;
        } else {
            log.info("Жанр с id '{}' не найден.", id);
            throw new ObjectNotFoundException(String.format(
                    "Жанр с id %s не найден.",
                    id
            ));
        }
    }

    public Collection<Genre> getAll() {
        final String sqlQuery = "select * from genres";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRowToGenre(rs, rowNum));
    }

    public void addFilmGenres(Set<Genre> filmGenres, int filmId) {
        final String sqlQuery = "insert into film_genres (film_id, genre_id) " +
                "values (?, ?)";
        for (Genre genre: filmGenres) {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery);
                stmt.setInt(1, filmId);
                stmt.setInt(2, genre.getId());
                return stmt;
            });
        }
    }

    public Set<Genre> getFilmGenres(int id) {
        String sqlQuery = "select genre_id " +
                "from film_genres where film_id = ?";
        SqlRowSet filmGenreRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        Set<Genre> filmGenres = new TreeSet<>(Comparator.comparingInt(Genre::getId));

        while(filmGenreRows.next()) {
            filmGenres.add(getById(filmGenreRows.getInt("genre_id")));
        }

        return filmGenres;
    }

    public void deleteFilmGenres(int id) {
        jdbcTemplate.update("delete from film_genres where film_id = ?", id);
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }
}
