package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public User getUser(int id) {
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            isSpaceInLogin(user);
            users.put(user.getId(), user);
            log.info("Выполнено обновление пользовтеля {}", user.getName());
            return user;
        }
        else
            log.error("Попытка обновления пользователя с несуществующим id {}", user.getId());
        throw new ValidationException("Пользователь с таким id не существует");
    }

    @Override
    public User register(User user) {
        isSpaceInLogin(user);
        user.setId(++idGenerator);
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь {}", user.getName());
        return user;
    }

    public void isSpaceInLogin (User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("В логине присутствуют пробелы");
            throw new ValidationException("В логине присутствуют пробелы");
        }
    }
}
