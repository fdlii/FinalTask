package advertisement.mappers;

import advertisement.DTOs.request.CategoryRequestDTO;
import advertisement.DTOs.response.CategoryResponseDTO;
import advertisement.models.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ICategoryDTOToModelMapper {
    CategoryResponseDTO toDTO(Category category);
    Category toModel(CategoryRequestDTO categoryRequestDTO);
}
