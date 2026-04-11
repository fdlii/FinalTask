package advertisement.daos.interfaces;

import java.util.List;

public interface IGenericDAO<T, ID> {
    List<T> findAll();
    T save(T entity);
}
