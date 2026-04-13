package advertisement.daos.implementations;

import advertisement.daos.interfaces.ICategoryDAO;
import advertisement.entities.CategoryEntity;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryDAO extends GenericDAO<CategoryEntity> implements ICategoryDAO {
    public CategoryDAO() {
        super(CategoryEntity.class);
    }
}
