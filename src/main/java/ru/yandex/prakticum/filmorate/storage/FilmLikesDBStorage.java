package ru.yandex.prakticum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;

@Component()
public class FilmLikesDBStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmLikesDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void add(int film_id, int userId) {
        final String sqlQuery = "insert into film_likes (film_id, user_id) " +
                "values (?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.setInt(1, film_id);
            stmt.setInt(2, userId);
            return stmt;
        });
    }

    public void delete(int film_id, int user_id) {
        jdbcTemplate.update("delete from film_likes where film_id = ? and user_id = ?", film_id, user_id);
    }
}
