package advertisement.services;

import advertisement.daos.interfaces.ICategoryDAO;
import advertisement.entities.CategoryEntity;
import advertisement.exceptions.other.CategoryAlreadyExistException;
import advertisement.mappers.ICategoryModelToEntityMapper;
import advertisement.models.Category;
import advertisement.services.implementations.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTestClass {
    @Mock
    private ICategoryDAO categoryDAO;
    @Mock
    private ICategoryModelToEntityMapper categoryModelToEntityMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryEntity categoryEntity;

    @BeforeEach
    void setUp() {
        category = Category.builder().name("Electronics").build();
        categoryEntity = CategoryEntity.builder().id(1L).name("Electronics").build();
    }

    @Test
    void addCategorySuccessfully() {
        //Given

        //When
        when(categoryDAO.findByName("Electronics")).thenReturn(Optional.empty());
        when(categoryModelToEntityMapper.toEntity(category)).thenReturn(categoryEntity);
        when(categoryDAO.save(categoryEntity)).thenReturn(categoryEntity);
        when(categoryModelToEntityMapper.toModel(categoryEntity)).thenReturn(category);

        //Then
        Category result = categoryService.addCategory(category);

        assertEquals(category, result);
        verify(categoryDAO).save(categoryEntity);
    }

    @Test
    void addCategoryCategoryAlreadyExistException() {
        //Given

        //When
        when(categoryDAO.findByName("Electronics")).thenReturn(Optional.of(categoryEntity));

        //Then
        assertThrows(CategoryAlreadyExistException.class,
                () -> categoryService.addCategory(category));

        verify(categoryDAO, never()).save(any());
    }
}