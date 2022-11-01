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
import ru.yandex.prakticum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

@Component("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage{

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User getById(int id) {
        String sqlQuery = "select email, login, name, birthday " +
                "from users where user_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if(userRows.next()) {
            User user = User.builder()
                    .id(id)
                    .email(userRows.getString("email"))
                    .login(userRows.getString("login"))
                    .name(userRows.getString("name"))
                    .birthday(userRows.getDate("birthday").toLocalDate())
                    .build();

            return user;
        } else {
            log.info("Пользователь с id '{}' не найден.", id);
            throw new ObjectNotFoundException(String.format(
                    "Пользователь с id %s не найден.",
                    id
            ));
        }
    }

    @Override
    public Collection<User> getAll() {
        final String sqlQuery = "select * from users";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRowToUser(rs, rowNum));
    }

    @Override
    public void add(User user) {
        final String sqlQuery = "insert into users (email, login, name, birthday) " +
                "values (?, ?, ?, ?)";

        int id = user.getId();

        if (id != 0) {
            checkUserIdNotNegative(id);
            checkUserAlreadyExist(id);
        }

        checkUserLogin(user.getLogin());
        checkUserBirthday(user.getBirthday());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, setNameByDefault(user));
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        if (id == 0) {
            id = Objects.requireNonNull(keyHolder.getKey()).intValue();
            user.setId(id);
        }

        log.info("Добавлен пользователь: '{}'", user.toString());
    }

    @Override
    public void update(User user) {
        String sqlQuery = "update users set " +
                "email = ?, login = ?, name = ?, birthday = ?" +
                "where user_id = ?";

        int id = user.getId();

        checkUserIdNotNull(id);
        checkUserIdNotNegative(id);
        checkUserNotFound(id);
        checkUserLogin(user.getLogin());
        checkUserBirthday(user.getBirthday());

        jdbcTemplate.update(sqlQuery
                ,user.getEmail()
                ,user.getLogin()
                ,user.getName()
                ,user.getBirthday()
                ,id);
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getInt("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    @Override
    public void checkUserNotFound(int id) {
        if (getById(id) == null) {
            log.info("Пользователь с id '{}' не найден.", id);
            throw new ObjectNotFoundException(String.format(
                    "Пользователь с id %s не найден.",
                    id
            ));
        }
    }

    private void checkUserAlreadyExist(int id) {
        if (getById(id) != null) {
            log.info("Пользователь с id '{}' уже существует.", id);
            throw new ObjectAlreadyExistException(String.format(
                    "Пользователь с id %s уже существует.",
                    id
            ));
        }
    }

    private void checkUserIdNotNull(int id) {
        if (id == 0) {
            log.info("id '{}' не заполнен.", id);
            throw new ValidationException("id не заполнен.");
        }
    }

    private void checkUserIdNotNegative(int id) {
        if (id < 0) {
            log.info("id '{}' не может быть отрицательным.", id);
            throw new ValidationException("id не может быть отрицательным.");
        }
    }

    private void checkUserLogin(String login) {
        if (login.contains(" ")) {
            log.info("Логин '{}' не может содержать пробелы.", login);
            throw new ValidationException("Логин не может содержать пробелы.");
        }
    }

    private void checkUserBirthday(LocalDate birthday) {
        if (birthday.isAfter(java.time.LocalDate.now()) ) {
            log.info("Дата рождения '{}' не может быть в будущем.", birthday);
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }

    private String setNameByDefault(User user) {
        String name = user.getName();
        if (name.isEmpty()) {
            user.setName("common");
            return user.getName();
        }

        return name;
    }
}
