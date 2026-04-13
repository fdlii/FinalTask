package advertisement.services.implementations;

import advertisement.daos.interfaces.ICategoryDAO;
import advertisement.mappers.ICategoryModelToEntityMapper;
import advertisement.models.Category;
import advertisement.services.interfaces.ICategoryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService implements ICategoryService {
    @Autowired
    private ICategoryDAO categoryDAO;
    @Autowired
    private ICategoryModelToEntityMapper categoryModelToEntityMapper;

    @Override
    @Transactional
    public Category addCategory(Category category) {
        return categoryModelToEntityMapper.toModel(categoryDAO.save(categoryModelToEntityMapper.toEntity(category)));
    }
}
