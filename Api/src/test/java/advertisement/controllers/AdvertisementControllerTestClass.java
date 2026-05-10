package advertisement.controllers;

import advertisement.AdvertisementFilter;
import advertisement.DTOs.request.AdvertisementManageRequestDTO;
import advertisement.DTOs.request.AdvertisementRequestDTO;
import advertisement.DTOs.request.UserRequestDTO;
import advertisement.DTOs.response.AdvertisementResponseDTO;
import advertisement.JwtHandler;
import advertisement.mappers.IAdvertisementDTOToModelMapper;
import advertisement.models.Advertisement;
import advertisement.security.LoginPasswordUserDetailsService;
import advertisement.security.exception.JwtAuthenticationEntryPoint;
import advertisement.services.interfaces.IAdvertisementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdvertisementController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdvertisementControllerTestClass {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IAdvertisementService advertisementService;

    @MockitoBean
    private IAdvertisementDTOToModelMapper advertisementDTOToModelMapper;

    @MockitoBean
    private JwtHandler jwtHandler;

    @MockitoBean
    private LoginPasswordUserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthenticationEntryPoint entryPoint;

    private UserRequestDTO validUserRequestDTO;
    private AdvertisementRequestDTO validAdvertisementRequestDTO;
    private AdvertisementResponseDTO advertisementResponseDTO;
    private Advertisement advertisement;

    @BeforeEach
    void setUp() {
        validUserRequestDTO = UserRequestDTO.builder()
                .login("test@mail.com")
                .password("password123")
                .username("testUser")
                .roles(Set.of("USER"))
                .build();

        validAdvertisementRequestDTO = AdvertisementRequestDTO.builder()
                .adNumber(1L)
                .user(validUserRequestDTO)
                .title("Sample title")
                .description("Sample description")
                .price(100.0)
                .country("Russia")
                .region("Moscow Oblast")
                .town("Moscow")
                .categories(List.of())
                .build();

        advertisement = Advertisement.builder()
                .adNumber(1L)
                .title("Sample title")
                .description("Sample description")
                .price(100.0)
                .country("Russia")
                .region("Moscow Oblast")
                .town("Moscow")
                .build();

        advertisementResponseDTO = AdvertisementResponseDTO.builder()
                .adNumber(1L)
                .title("Sample title")
                .description("Sample description")
                .price(100.0)
                .country("Russia")
                .region("Moscow Oblast")
                .town("Moscow")
                .closed(false)
                .build();
    }

    // ---------- getAdvertisements ----------

    @Test
    void getAdvertisementsSuccessfully() throws Exception {
        //Given

        //When
        when(advertisementService.getAdvertisements(any(AdvertisementFilter.class)))
                .thenReturn(List.of(advertisement));
        when(advertisementDTOToModelMapper.toDTOList(anyList()))
                .thenReturn(List.of(advertisementResponseDTO));

        //Then
        mockMvc.perform(get("/advertisement")
                        .param("title", "Sample")
                        .param("town", "Moscow"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].adNumber").value(1))
                .andExpect(jsonPath("$[0].title").value("Sample title"));

        verify(advertisementService).getAdvertisements(any(AdvertisementFilter.class));
    }

    // ---------- getSalesHistory ----------

    @Test
    void getSalesHistorySuccessfully() throws Exception {
        //Given

        //When
        when(advertisementService.getSalesHistory("test@mail.com"))
                .thenReturn(List.of(advertisement));
        when(advertisementDTOToModelMapper.toDTOList(anyList()))
                .thenReturn(List.of(advertisementResponseDTO));

        //Then
        mockMvc.perform(get("/advertisement/{user_login}", "test@mail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].adNumber").value(1))
                .andExpect(jsonPath("$[0].title").value("Sample title"));

        verify(advertisementService, atLeastOnce()).getSalesHistory("test@mail.com");
    }

    @Test
    void getSalesHistoryInvalidEmailFormat() throws Exception {
        //Given

        //When

        //Then
        mockMvc.perform(get("/advertisement/{user_login}", "not-an-email"))
                .andExpect(status().isBadRequest());
    }

    // ---------- addAdvertisement ----------

    @Test
    void addAdvertisementSuccessfully() throws Exception {
        //Given
        MockMultipartFile dataPart = new MockMultipartFile(
                "data", "data", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(validAdvertisementRequestDTO)
        );
        MockMultipartFile filePart = new MockMultipartFile(
                "file", "preview.png", MediaType.IMAGE_PNG_VALUE, "image-content".getBytes()
        );

        //When
        when(advertisementDTOToModelMapper.toModel(any(AdvertisementRequestDTO.class)))
                .thenReturn(advertisement);
        when(advertisementService.addAdvertisement(any(Advertisement.class), any(MultipartFile.class)))
                .thenReturn(advertisement);
        when(advertisementDTOToModelMapper.toDTO(any(Advertisement.class)))
                .thenReturn(advertisementResponseDTO);

        //Then
        mockMvc.perform(multipart("/advertisement")
                        .file(dataPart)
                        .file(filePart))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adNumber").value(1))
                .andExpect(jsonPath("$.title").value("Sample title"));

        verify(advertisementService).addAdvertisement(any(Advertisement.class), any(MultipartFile.class));
    }

    @Test
    void addAdvertisementBlankTitle() throws Exception {
        //Given
        validAdvertisementRequestDTO.setTitle("");
        MockMultipartFile dataPart = new MockMultipartFile(
                "data", "data", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(validAdvertisementRequestDTO)
        );

        //When

        //Then
        mockMvc.perform(multipart("/advertisement").file(dataPart))
                .andExpect(status().isBadRequest());
    }

    // ---------- editAdvertisement ----------

    @Test
    void editAdvertisementSuccessfully() throws Exception {
        //Given
        MockMultipartFile dataPart = new MockMultipartFile(
                "data", "data", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(validAdvertisementRequestDTO)
        );
        MockMultipartFile filePart = new MockMultipartFile(
                "file", "preview.png", MediaType.IMAGE_PNG_VALUE, "image-content".getBytes()
        );

        //When
        when(advertisementDTOToModelMapper.toModel(any(AdvertisementRequestDTO.class)))
                .thenReturn(advertisement);
        when(advertisementService.editAdvertisement(any(Advertisement.class), any(MultipartFile.class)))
                .thenReturn(advertisement);
        when(advertisementDTOToModelMapper.toDTO(any(Advertisement.class)))
                .thenReturn(advertisementResponseDTO);

        //Then
        MockMultipartHttpServletRequestBuilder builder = multipart("/advertisement");
        builder.with(request -> { request.setMethod("PUT"); return request; });

        mockMvc.perform(builder.file(dataPart).file(filePart))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adNumber").value(1))
                .andExpect(jsonPath("$.title").value("Sample title"));

        verify(advertisementService).editAdvertisement(any(Advertisement.class), any(MultipartFile.class));
    }

    @Test
    void editAdvertisementNegativePrice() throws Exception {
        //Given
        validAdvertisementRequestDTO.setPrice(-10.0);
        MockMultipartFile dataPart = new MockMultipartFile(
                "data", "data", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(validAdvertisementRequestDTO)
        );

        //When

        //Then
        MockMultipartHttpServletRequestBuilder builder = multipart("/advertisement");
        builder.with(request -> { request.setMethod("PUT"); return request; });

        mockMvc.perform(builder.file(dataPart))
                .andExpect(status().isBadRequest());
    }

    // ---------- prepayAdvertisement ----------

    @Test
    void prepayAdvertisementSuccessfully() throws Exception {
        //Given

        //When
        doNothing().when(advertisementService).prepayAdvertisement(1L);

        //Then
        mockMvc.perform(put("/advertisement/prepay/{adNumber}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Объявление успешно проплачено."));

        verify(advertisementService).prepayAdvertisement(1L);
    }

    // ---------- closeAdvertisement ----------

    @Test
    void closeAdvertisementSuccessfully() throws Exception {
        //Given
        AdvertisementManageRequestDTO requestDTO = AdvertisementManageRequestDTO.builder()
                .adNumber(1L)
                .user(validUserRequestDTO)
                .build();

        //When
        doNothing().when(advertisementService).closeAdvertisement(1L, "test@mail.com");

        //Then
        mockMvc.perform(put("/advertisement/close")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Объявление успешно закрыто."));

        verify(advertisementService).closeAdvertisement(1L, "test@mail.com");
    }

    @Test
    void closeAdvertisementNullUser() throws Exception {
        //Given
        AdvertisementManageRequestDTO requestDTO = AdvertisementManageRequestDTO.builder()
                .adNumber(1L)
                .user(null)
                .build();

        //When

        //Then
        mockMvc.perform(put("/advertisement/close")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    // ---------- deleteAdvertisement ----------

    @Test
    void deleteAdvertisementSuccessfully() throws Exception {
        //Given

        //When
        doNothing().when(advertisementService).deleteAdvertisement(1L);

        //Then
        mockMvc.perform(delete("/advertisement/{adNumber}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Объявление успешно удалено."));

        verify(advertisementService).deleteAdvertisement(1L);
    }
}