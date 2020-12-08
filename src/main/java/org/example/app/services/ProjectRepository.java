package org.example.app.services;

import org.example.web.dto.ShelfFilter;

import java.util.List;

public interface ProjectRepository<T> {
    List<T> retreiveAll();

    void store(T book);

    boolean removeItemById(Integer bookIdToRemove);

    List<T> findBy(ShelfFilter request);

    boolean remove(ShelfFilter books);
}
