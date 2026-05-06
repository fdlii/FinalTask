package advertisement.controllers;

import advertisement.DTOs.request.CategoryRequestDTO;
import advertisement.DTOs.response.CategoryResponseDTO;
import advertisement.mappers.ICategoryDTOToModelMapper;
import advertisement.services.interfaces.ICategoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
public class CategoryController {
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private ICategoryDTOToModelMapper categoryDTOToModelMapper;
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> addCategory(@Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {
        logger.info("Добавление категории.");
        CategoryResponseDTO response = categoryDTOToModelMapper
                .toDTO(categoryService
                        .addCategory(categoryDTOToModelMapper
                                .toModel(categoryRequestDTO)
                        )
                );
        return ResponseEntity.ok(response);
    }
}
