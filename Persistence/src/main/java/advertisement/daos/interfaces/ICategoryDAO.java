package advertisement.daos.interfaces;

import advertisement.entities.CategoryEntity;

import java.util.Optional;

public interface ICategoryDAO extends IGenericDAO<CategoryEntity> {

    Optional<CategoryEntity> findByName(String name);
}
