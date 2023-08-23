package dev.deyve.grainpayapi.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IService<T> {

    public Page<T> findAll(Pageable pageable);

    T save(T t);

    T findById(Long id);

    public T updateById(Long id, T t);

    void deleteById(Long id);

}
