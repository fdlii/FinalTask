package advertisement.daos.implementations;

import advertisement.daos.interfaces.IGenericDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public abstract class GenericDAO<T, ID> implements IGenericDAO<T, ID> {
    @PersistenceContext
    protected EntityManager entityManager;

    private final Class<T> entityClass;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public List<T> findAll() {
        String jpql = "SELECT e FROM " + entityClass.getName() + " e";
        return entityManager.createQuery(jpql, entityClass)
                .getResultList();
    }

    @Override
    public T save(T entity) {
        entityManager.persist(entity);
        return entity;
    }
}
