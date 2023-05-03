package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private Map<Integer, User> users = new HashMap<>();
    private int idGenerator = 0;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUser(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Выполнено обновление пользовтеля {}", user.getName());
            return user;
        } else {
            throw new UserNotFoundException("Пользователь с таким id не существует");
        }

    }

    @Override
    public User register(User user) {
        user.setId(++idGenerator);
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь {}", user.getName());
        return user;
    }

    @Override
    public void removeUser(int userId) {
        //пока нет логики
    }
}
