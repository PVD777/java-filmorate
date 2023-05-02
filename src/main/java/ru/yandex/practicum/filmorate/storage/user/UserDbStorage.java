package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@AllArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = (resultSet, rowNum) -> {
        User user = new User(
                resultSet.getString("email"),
                resultSet.getString("login"),
                resultSet.getString("name"),
                LocalDate.parse(resultSet.getString("birthday"))
        );
        user.setId(resultSet.getInt("user_id"));
        return user;
    };

    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query("SELECT * FROM USERS", userRowMapper);
    }

    @Override
    public Optional<User> getUser(int id) {
        if (!isUserExists(id)) {
            log.info("Пользователь с идентификатором {} отсутствует.", id);
            throw new UserNotFoundException("Пользователь с id " + id + " не найден");
        }
        User user = jdbcTemplate.queryForObject("SELECT * FROM USERS WHERE USER_ID = ?",
                userRowMapper, id);
        Set<Integer> friendsId = new HashSet<>();
        String sql = "SELECT friend_id FROM FRIENDS WHERE USER_ID = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        while (rowSet.next()) {
            friendsId.add(rowSet.getInt("friend_id"));
        }
        user.setFriedndsId(friendsId);
        log.info("Найден пользователь: {} {}", user.getId(),user.getLogin());
        return Optional.of(user);
    }

    @Override
    public User updateUser(User user) {
        if (!isUserExists(user.getId())) {
            log.info("Пользователь с идентификатором {} отсутствует.", user.getId());
            throw new UserNotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
            String sql = "UPDATE USERS SET login = ?, name = ?, email = ?, birthday = ? WHERE user_id = "
                    + user.getId();
            jdbcTemplate.update(sql, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday());
            log.info("Пользователь с идентификатором {} обновлен.", user.getId());

        return user;
    }

    @Override
    public User register(User user) {
        Map<String, Object> keys = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingColumns("login", "name", "email", "birthday")
                .usingGeneratedKeyColumns("user_id")
                .executeAndReturnKeyHolder(Map.of("login", user.getLogin(),
                        "name", user.getName(),
                        "email", user.getEmail(), "birthday", user.getBirthday()))
                .getKeys();
        user.setId((Integer) keys.get("user_id"));
        log.info("Добавлен новый пользователь: id={}", user.getId());
        return  user;
    }

    @Override
    public void removeUser(int userId) {
        if (!isUserExists(userId)) {
            log.info("Удаление пользователя с id " + userId);
            throw new UserNotFoundException("Указанный пользователь не существует");
        }
            String sql = "DELETE FROM USERS WHERE user_id = ?";
            jdbcTemplate.update(sql, userId);
            log.info("Пользователь с id = " + userId + " удален");
            return;
    }

    private boolean isUserExists(int id) {
        String sql = "SELECT count(*) FROM USERS WHERE user_id = ?";
        int count = jdbcTemplate.queryForObject(sql, new Object[] { id }, Integer.class);
        return count > 0;
    }
}
