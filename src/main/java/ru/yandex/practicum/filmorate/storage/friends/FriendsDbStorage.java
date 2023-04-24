package ru.yandex.practicum.filmorate.storage.friends;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;

import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class FriendsDbStorage implements FriendsStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addToFriend(int user1id, int user2id) {
        if (checkforExists(user1id, user2id)) {
            log.info("Попытка подружить несуществующих пользователей  id = {} и Id = {} ", user1id, user2id);
            throw new UserNotFoundException("Указанный пользователь не существует");
        }

        if (checkForFriend(user1id, user2id) || checkForFriend(user2id, user1id)) {
            log.info("Пользователь  id = {} и Id = {} уже друзья", user1id, user2id);
            return;
        }
        if (checkForFollowers(user1id, user2id)) {
            log.info("Пользователь id = {} ПОВТОРНО отправил запрос на дружбу Id = {}", user1id, user2id);
            return;
        }
        if (checkForFollowers(user2id, user1id)) {

            String sql = "UPDATE FRIENDS SET friend_status = True WHERE user_id = "
                    + user2id + " AND friend_id = " + user1id;
            jdbcTemplate.update(sql);
            log.info("Пользователь id = {} подтвердил запрос на дружбу с Id = {}", user2id, user1id);
            return;
        }
            new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("FRIENDS")
                    .usingColumns("user_id", "friend_id")
                    .execute(Map.of("user_id", user1id,
                            "friend_id", user2id));

            log.info("Пользователь id = {} отправил запрос на дружбу Id = {}", user1id, user2id);
    }

    @Override
    public void removeFromFriend(int user1id, int user2id) {
        if (checkforExists(user1id, user2id)) {
            log.info("Попытка поссорить несуществующих пользователей  id = {} и Id = {} ", user1id, user2id);
            throw new UserNotFoundException("Указанный пользователь не существует");
        }
        if (checkForFriend(user1id, user2id) || checkForFriend(user2id, user1id)) {
            String sql = "UPDATE FRIENDS SET user_id = ?, friend_id = ?, friend_status = ? WHERE user_id = "
                    + user1id + " AND friend_id = " + user2id;
            jdbcTemplate.update(sql,user2id,user1id,false);
            log.info("Пользователь  id = {} удалил из друзей Id = {}", user1id, user2id);
            return;
        }

        if (checkForFollowers(user1id, user2id)) {
            String sql = "DELETE FROM FRIENDS WHERE user_id = ? AND  friend_id = ?";
            jdbcTemplate.update(sql, user1id,user2id);
            log.info("Пользователь id = {} удалил неподтвержденный запрос на дружбу с Id = {}", user1id, user2id);
            return;
        }
        log.info("Пользователи id = {} и Id = {} не являются друзьями", user1id, user2id);
    }


    private boolean checkForFriend(int userId, int friendId) {
        String sqlQuery = "SELECT * FROM FRIENDS WHERE (user_id = ? AND  friend_id = ?)";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId);
        return (rowSet.first() && rowSet.getBoolean("friend_status"));
    }

    private boolean checkForFollowers(int userId, int friendId) {
        String sqlQuery = "SELECT * FROM FRIENDS WHERE (user_id = ? AND  friend_id = ?)";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, userId, friendId);
        return (rowSet.first());
    }

    private boolean checkforExists(int userId, int friendId) {
        String sql = "SELECT count(*) FROM USERS WHERE user_id in(?,?)";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId, friendId);
        rowSet.next();
        return rowSet.getInt("count(*)") != 2;
    }
}
