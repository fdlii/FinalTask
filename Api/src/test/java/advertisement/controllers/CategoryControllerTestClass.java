package advertisement.controllers;

import advertisement.DTOs.request.CategoryRequestDTO;
import advertisement.DTOs.response.CategoryResponseDTO;
import advertisement.JwtHandler;
import advertisement.mappers.ICategoryDTOToModelMapper;
import advertisement.models.Category;
import advertisement.security.LoginPasswordUserDetailsService;
import advertisement.security.exception.JwtAuthenticationEntryPoint;
import advertisement.services.interfaces.ICategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTestClass {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ICategoryService categoryService;

    @MockitoBean
    private ICategoryDTOToModelMapper categoryDTOToModelMapper;

    @MockitoBean
    private JwtHandler jwtHandler;

    @MockitoBean
    private LoginPasswordUserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthenticationEntryPoint entryPoint;

    private CategoryRequestDTO validCategoryRequestDTO;
    private CategoryResponseDTO categoryResponseDTO;
    private Category category;

    @BeforeEach
    void setUp() {
        validCategoryRequestDTO = CategoryRequestDTO.builder()
                .name("Electronics")
                .build();

        category = Category.builder()
                .name("Electronics")
                .build();

        categoryResponseDTO = CategoryResponseDTO.builder()
                .name("Electronics")
                .build();
    }

    // ---------- addCategory ----------

    @Test
    void addCategorySuccessfully() throws Exception {
        //Given

        //When
        when(categoryDTOToModelMapper.toModel(any(CategoryRequestDTO.class)))
                .thenReturn(category);
        when(categoryService.addCategory(any(Category.class)))
                .thenReturn(category);
        when(categoryDTOToModelMapper.toDTO(any(Category.class)))
                .thenReturn(categoryResponseDTO);

        //Then
        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(validCategoryRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Electronics"));

        verify(categoryService).addCategory(any(Category.class));
    }

    @Test
    void addCategoryBlankName() throws Exception {
        //Given
        validCategoryRequestDTO.setName("");

        //When

        //Then
        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(validCategoryRequestDTO)))
                .andExpect(status().isBadRequest());
    }
}