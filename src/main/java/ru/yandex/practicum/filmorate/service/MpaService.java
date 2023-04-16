package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MpaService {

    private final MpaStorage mpaStorage;

    public List<Mpa> getallMpa() {
        return mpaStorage.getAllMpa();
    }

    public Mpa getMpa(int id) {
        return mpaStorage.getMpa(id).orElseThrow(() -> new MpaNotFoundException("Mpa отсутствует"));
    }
}
