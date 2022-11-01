package ru.yandex.prakticum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class FriendsDBStorage {
    private final JdbcTemplate jdbcTemplate;

    public FriendsDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void add(int id, int friendId) {
        final String sqlQuery = "insert into user_friends (user_id, friend_id) " +
                "values (?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.setInt(1, id);
            stmt.setInt(2, friendId);
            return stmt;
        });
    }

    public void delete(int id, int friendId) {
        jdbcTemplate.update("delete from user_friends where user_id = ? and friend_id = ?", id, friendId);
    }

    public List<Integer> getUserFriendsIds(int id) {
        final String sqlQuery = "select friend_id from user_friends where user_id = ?";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapFriendToUser(rs, rowNum), id);
    }

    private int mapFriendToUser(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt("friend_id");
    }
}
