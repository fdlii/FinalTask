package advertisement.daos.interfaces;

import java.util.List;

public interface IGenericDAO<T> {
    List<T> findAll();
    T save(T entity);
    T delete(T entity);
    T update(T entity);
}
