package advertisement.services.implementations;

import advertisement.daos.interfaces.ICategoryDAO;
import advertisement.entities.CategoryEntity;
import advertisement.exceptions.other.CategoryAlreadyExistException;
import advertisement.mappers.ICategoryModelToEntityMapper;
import advertisement.models.Category;
import advertisement.services.interfaces.ICategoryService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService implements ICategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    @Autowired
    private ICategoryDAO categoryDAO;
    @Autowired
    private ICategoryModelToEntityMapper categoryModelToEntityMapper;

    @Override
    @Transactional
    public Category addCategory(Category category) {
        Optional<CategoryEntity> optionalCategoryEntity = categoryDAO.findByName(category.getName());
        if (optionalCategoryEntity.isPresent()) {
            logger.error("Указанная категория уже существует.");
            throw new CategoryAlreadyExistException("Указанная категория уже существует.");
        }

        logger.info("Категория успешно добавлена.");
        return categoryModelToEntityMapper.toModel(categoryDAO.save(categoryModelToEntityMapper.toEntity(category)));
    }
}
