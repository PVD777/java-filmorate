package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface MpaStorage {

    public List<Mpa> getAllMpa();

    public Optional<Mpa> getMpa(int id);

    public Mpa updateMpa(Mpa mpa);

    public Mpa addMpa(Mpa mpa);

}
