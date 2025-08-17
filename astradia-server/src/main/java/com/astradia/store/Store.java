package com.astradia.store;

import java.util.*;

public interface Store<T, ID> {
    void save(ID id, T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void delete(ID id);
    void saveAll(Map<ID, T> entries);
}