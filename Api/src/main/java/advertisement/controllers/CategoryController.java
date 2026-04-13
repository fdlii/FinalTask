package advertisement.controllers;

import advertisement.DTOs.request.CategoryRequestDTO;
import advertisement.DTOs.response.CategoryResponseDTO;
import advertisement.mappers.ICategoryDTOToModelMapper;
import advertisement.services.interfaces.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private ICategoryDTOToModelMapper categoryDTOToModelMapper;
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> addCategory(@RequestBody CategoryRequestDTO categoryRequestDTO) {
        CategoryResponseDTO response = categoryDTOToModelMapper
                .toDTO(categoryService
                        .addCategory(categoryDTOToModelMapper
                                .toModel(categoryRequestDTO)
                        )
                );
        return ResponseEntity.ok(response);
    }
}
