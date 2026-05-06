package advertisement.daos.implementations;

import advertisement.daos.interfaces.ICategoryDAO;
import advertisement.entities.CategoryEntity;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CategoryDAO extends GenericDAO<CategoryEntity> implements ICategoryDAO {
    public CategoryDAO() {
        super(CategoryEntity.class);
    }

    @Override
    public Optional<CategoryEntity> findByName(String name) {
        String jpql = "SELECT c FROM CategoryEntity c WHERE c.name = :name";
        TypedQuery<CategoryEntity> query = entityManager.createQuery(jpql, CategoryEntity.class);
        query.setParameter("name", name);
        return query.getResultList().stream().findFirst();
    }
}
