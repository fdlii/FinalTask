package advertisement.controllers;

import advertisement.DTOs.request.LoginPasswordRequestDTO;
import advertisement.DTOs.request.UserRequestDTO;
import advertisement.DTOs.response.UserResponseDTO;
import advertisement.JwtHandler;
import advertisement.mappers.IUserDTOToModelMapper;
import advertisement.models.User;
import advertisement.security.LoginPasswordUserDetailsService;
import advertisement.security.exception.JwtAuthenticationEntryPoint;
import advertisement.services.interfaces.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTestClass {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IUserService userService;

    @MockitoBean
    private IUserDTOToModelMapper userMapper;

    @MockitoBean
    private JwtHandler jwtHandler;

    @MockitoBean
    private LoginPasswordUserDetailsService userDetailsService;

    @MockitoBean
    private JwtAuthenticationEntryPoint entryPoint;

    private UserRequestDTO validUserRequestDTO;
    private UserResponseDTO userResponseDTO;
    private User user;

    @BeforeEach
    void setUp() {
        validUserRequestDTO = UserRequestDTO.builder()
                .login("test@mail.com")
                .password("password123")
                .username("testUser")
                .country("Russia")
                .region("Moscow Oblast")
                .town("Moscow")
                .roles(Set.of("USER"))
                .build();

        user = User.builder()
                .login("test@mail.com")
                .password("password123")
                .username("testUser")
                .country("Russia")
                .region("Moscow Oblast")
                .town("Moscow")
                .roles(Set.of("USER"))
                .build();

        userResponseDTO = UserResponseDTO.builder()
                .login("test@mail.com")
                .username("testUser")
                .country("Russia")
                .region("Moscow Oblast")
                .town("Moscow")
                .sellerRating(0.0)
                .avatarUrl("http://example.com/avatar.png")
                .build();
    }

    // ---------- registerUser ----------

    @Test
    void registerUserSuccessfully() throws Exception {
        //Given
        MockMultipartFile dataPart = new MockMultipartFile(
                "data", "data", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(validUserRequestDTO)
        );
        MockMultipartFile filePart = new MockMultipartFile(
                "file", "avatar.png", MediaType.IMAGE_PNG_VALUE, "image-content".getBytes()
        );

        //When
        when(userMapper.toUser(any(UserRequestDTO.class))).thenReturn(user);
        when(userService.registerUser(any(User.class), any(MultipartFile.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userResponseDTO);

        //Then
        mockMvc.perform(multipart("/user/register")
                        .file(dataPart)
                        .file(filePart))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("test@mail.com"))
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.avatarUrl").value("http://example.com/avatar.png"));

        verify(userService).registerUser(any(User.class), any(MultipartFile.class));
    }

    @Test
    void registerUserInvalidEmailFormat() throws Exception {
        //Given
        validUserRequestDTO.setLogin("not-an-email");
        MockMultipartFile dataPart = new MockMultipartFile(
                "data", "data", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(validUserRequestDTO)
        );

        //When

        //Then
        mockMvc.perform(multipart("/user/register").file(dataPart))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUserEmptyRoles() throws Exception {
        //Given
        validUserRequestDTO.setRoles(Set.of());
        MockMultipartFile dataPart = new MockMultipartFile(
                "data", "data", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(validUserRequestDTO)
        );

        //When

        //Then
        mockMvc.perform(multipart("/user/register").file(dataPart))
                .andExpect(status().isBadRequest());
    }

    // ---------- loginUser ----------

    @Test
    void loginUserSuccessfully() throws Exception {
        //Given
        LoginPasswordRequestDTO requestDTO = new LoginPasswordRequestDTO("test@mail.com", "password123");

        //When
        when(userService.verifyUser("test@mail.com", "password123")).thenReturn("jwt-token");

        //Then
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("jwt-token"));

        verify(userService).verifyUser("test@mail.com", "password123");
    }

    @Test
    void loginUserBlankLogin() throws Exception {
        //Given
        LoginPasswordRequestDTO requestDTO = new LoginPasswordRequestDTO("", "password123");

        //When

        //Then
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginUserTooShortPassword() throws Exception {
        //Given
        LoginPasswordRequestDTO requestDTO = new LoginPasswordRequestDTO("test@mail.com", "short");

        //When

        //Then
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    // ---------- changePassword ----------

    @Test
    void changePasswordSuccessfully() throws Exception {
        //Given
        LoginPasswordRequestDTO requestDTO = new LoginPasswordRequestDTO("test@mail.com", "newPassword123");

        //When
        doNothing().when(userService).changePassword("test@mail.com", "newPassword123");

        //Then
        mockMvc.perform(put("/user/change_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Пароль успешно изменён."));

        verify(userService).changePassword("test@mail.com", "newPassword123");
    }

    // ---------- editProfile ----------

    @Test
    void editProfileSuccessfully() throws Exception {
        //Given
        MockMultipartFile dataPart = new MockMultipartFile(
                "data", "data", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(validUserRequestDTO)
        );
        MockMultipartFile filePart = new MockMultipartFile(
                "file", "avatar.png", MediaType.IMAGE_PNG_VALUE, "image-content".getBytes()
        );

        //When
        when(userMapper.toUser(any(UserRequestDTO.class))).thenReturn(user);
        when(userService.editProfile(any(User.class), any(MultipartFile.class))).thenReturn(user);
        when(userMapper.toDTO(any(User.class))).thenReturn(userResponseDTO);

        //Then
        MockMultipartHttpServletRequestBuilder builder = multipart("/user/edit_profile");
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(builder.file(dataPart).file(filePart))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("test@mail.com"))
                .andExpect(jsonPath("$.username").value("testUser"));

        verify(userService).editProfile(any(User.class), any(MultipartFile.class));
    }

    @Test
    void editProfileBlankLogin() throws Exception {
        //Given
        validUserRequestDTO.setLogin("");
        MockMultipartFile dataPart = new MockMultipartFile(
                "data", "data", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(validUserRequestDTO)
        );

        //When

        //Then
        MockMultipartHttpServletRequestBuilder builder = multipart("/user/edit_profile");
        builder.with(request -> { request.setMethod("PUT"); return request; });

        mockMvc.perform(builder.file(dataPart))
                .andExpect(status().isBadRequest());
    }
}