package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage storage;

    public List<Director> getAll() {
        return storage.getAll();
    }

    public Director getById(Integer id) {
        return storage.getById(id);
    }

    public Director addDirector(Director director) {
        return storage.addDirector(director);
    }

    public Director updateDirector(Director director) {
        return storage.updateDirector(director);
    }

    public void deleteDirector(Integer id) {
        storage.deleteDirector(id);
    }
}
