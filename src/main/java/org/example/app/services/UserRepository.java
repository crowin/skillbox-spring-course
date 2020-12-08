package org.example.app.services;

import java.util.List;

public interface UserRepository<T> {
    List<T> retrieveAll();

    void store(T user);

    T retrieveOne(String name);
}
