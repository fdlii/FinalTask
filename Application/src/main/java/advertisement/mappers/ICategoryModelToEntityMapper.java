package advertisement.mappers;

import advertisement.entities.CategoryEntity;
import advertisement.models.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ICategoryModelToEntityMapper {
    Category toModel(CategoryEntity categoryEntity);
    CategoryEntity toEntity(Category category);
}
