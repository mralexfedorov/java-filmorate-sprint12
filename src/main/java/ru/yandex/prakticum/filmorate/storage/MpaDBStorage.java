package ru.yandex.prakticum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.prakticum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.prakticum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component()
@Slf4j
public class MpaDBStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mpa getById(int id) {
        String sqlQuery = "select name, description " +
                "from Mpa where id = ?";
        SqlRowSet MpaRows = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if(MpaRows.next()) {
            Mpa mpa = Mpa.builder()
                    .id(id)
                    .name(MpaRows.getString("name"))
                    .description(MpaRows.getString("description"))
                    .build();

            return mpa;
        } else {
            log.info("Рейтинг с id '{}' не найден.", id);
            throw new ObjectNotFoundException(String.format(
                    "Рейтинг с id %s не найден.",
                    id
            ));
        }
    }

    public Collection<Mpa> getAll() {
        final String sqlQuery = "select * from Mpa";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRowToMpa(rs, rowNum));
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .build();
    }
}
