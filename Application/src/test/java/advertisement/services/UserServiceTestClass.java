package advertisement.services;

import advertisement.AdminConfig;
import advertisement.JwtHandler;
import advertisement.daos.implementations.RoleDAO;
import advertisement.daos.implementations.UserDAO;
import advertisement.entities.RoleEntity;
import advertisement.entities.UserEntity;
import advertisement.exceptions.notfound.RoleNotFoundException;
import advertisement.exceptions.notfound.UserNotFoundException;
import advertisement.exceptions.other.UserAlreadyExistException;
import advertisement.files.interfaces.IFileManager;
import advertisement.mappers.IUserModelToEntityMapper;
import advertisement.models.User;
import advertisement.services.implementations.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTestClass {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtHandler jwtHandler;
    @Mock
    private AdminConfig adminConfig;
    @Mock
    private IUserModelToEntityMapper userMapper;
    @Mock
    private RoleDAO roleDAO;
    @Mock
    private UserDAO userDAO;
    @Mock
    private IFileManager fileManager;
    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserEntity userEntity;
    private RoleEntity userRole;
    private RoleEntity adminRole;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .login("ngusev341@mail.ru")
                .password("12345678")
                .username("nikita")
                .country("Russia")
                .region("Moscow Oblast")
                .town("Moscow")
                .roles(Set.of("ROLE_USER"))
                .build();

        userEntity = UserEntity.builder()
                .id(1L)
                .login("ngusev341@mail.ru")
                .password("encodedPassword")
                .username("nikita")
                .country("Russia")
                .region("Moscow Oblast")
                .town("Moscow")
                .avatarLink("oldAvatar.png")
                .build();

        userRole = new RoleEntity();
        userRole.setName("ROLE_USER");

        adminRole = new RoleEntity();
        adminRole.setName("ROLE_ADMIN");
    }

    // ---------- registerUser ----------

    @Test
    void registerUserSuccessfully() throws IOException, IllegalAccessException {
        //Given

        //When
        when(userDAO.findByLogin(user.getLogin())).thenReturn(Optional.empty());
        when(roleDAO.findAll()).thenReturn(List.of(userRole, adminRole));
        when(fileManager.saveAvatar(multipartFile)).thenReturn("newAvatar.png");
        when(userMapper.toEntity(user)).thenReturn(userEntity);

        //Then
        User result = userService.registerUser(user, multipartFile);

        assertNotNull(result);
        assertEquals("ngusev341@mail.ru", result.getLogin());
        assertEquals("newAvatar.png", result.getAvatarLink());
        verify(fileManager).saveAvatar(multipartFile);
        verify(userDAO).save(eq(userEntity), anyList());
    }

    @Test
    void registerUserUserAlreadyExistException() {
        //Given

        //When
        when(userDAO.findByLogin(user.getLogin())).thenReturn(Optional.of(userEntity));

        //Then
        assertThrows(UserAlreadyExistException.class,
                () -> userService.registerUser(user, multipartFile));

        verify(userDAO, never()).save(any(), anyList());
    }

    @Test
    void registerUserIllegalAccessException() {
        //Given
        User adminUser = User.builder()
                .login("ngusev342@mail.ru")
                .password("12345679")
                .username("Admin")
                .roles(Set.of("ROLE_ADMIN"))
                .secretAdminKey("wrongKey")
                .build();

        //When
        when(userDAO.findByLogin(adminUser.getLogin())).thenReturn(Optional.empty());
        when(roleDAO.findAll()).thenReturn(List.of(userRole, adminRole));
        when(adminConfig.getApiKey()).thenReturn("correctKey");

        //Then
        assertThrows(IllegalAccessException.class,
                () -> userService.registerUser(adminUser, multipartFile));

        verify(userDAO, never()).save(any(), anyList());
    }

    @Test
    void registerUserRoleNotFoundException() {
        //Given
        User userWithUnknownRole = User.builder()
                .login("ngusev343@mail.ru")
                .password("325435234")
                .username("Nikita")
                .roles(Set.of("ROLE_UNKNOWN"))
                .build();

        //When
        when(userDAO.findByLogin(userWithUnknownRole.getLogin())).thenReturn(Optional.empty());
        when(roleDAO.findAll()).thenReturn(List.of(userRole, adminRole));

        //Then
        assertThrows(RoleNotFoundException.class,
                () -> userService.registerUser(userWithUnknownRole, multipartFile));

        verify(userDAO, never()).save(any(), anyList());
    }

    // ---------- verifyUser ----------

    @Test
    void verifyUserSuccessfully() {
        //Given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "ngusev341@mail.ru", "12345678", List.of());

        //When
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        userEntity.setRoles(List.of(userRole));
        when(userDAO.findByLogin("ngusev341@mail.ru")).thenReturn(Optional.of(userEntity));
        when(jwtHandler.generateToken(eq("ngusev341@mail.ru"), anyCollection())).thenReturn("jwt-token");

        //Then
        String token = userService.verifyUser("ngusev341@mail.ru", "12345678");

        assertEquals("jwt-token", token);
        verify(jwtHandler).generateToken(eq("ngusev341@mail.ru"), anyCollection());
    }

    @Test
    void verifyUserAuthenticationException() {
        //Given

        //When
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        //Then
        assertThrows(AuthenticationException.class,
                () -> userService.verifyUser("ngusev341@mail.ru", "wrongPassword"));

        verify(jwtHandler, never()).generateToken(anyString(), anyCollection());
    }

    // ---------- changePassword ----------

    @Test
    void changePasswordSuccessfully() {
        //Given

        //When
        when(userDAO.findByLogin("ngusev341@mail.ru")).thenReturn(Optional.of(userEntity));

        //Then
        userService.changePassword("ngusev341@mail.ru", "87654321");

        verify(userDAO).update(userEntity);
        assertNotNull(userEntity.getPassword());
        assertTrue(userEntity.getPassword().startsWith("$2a$"));
    }

    @Test
    void changePasswordUserNotFoundException() {
        //Given

        //When
        when(userDAO.findByLogin("sdjnnvlren@mail.ru")).thenReturn(Optional.empty());

        //Then
        assertThrows(UserNotFoundException.class,
                () -> userService.changePassword("sdjnnvlren@mail.ru", "87654321"));

        verify(userDAO, never()).update(any());
    }

    // ---------- editProfile ----------

    @Test
    void editProfileSuccessfully() throws IOException {
        //Given

        //When
        when(userDAO.findByLogin(user.getLogin())).thenReturn(Optional.of(userEntity));
        when(fileManager.saveAvatar(multipartFile)).thenReturn("updatedAvatar.png");

        //Then
        User result = userService.editProfile(user, multipartFile);

        assertEquals("updatedAvatar.png", result.getAvatarLink());
        assertEquals("updatedAvatar.png", userEntity.getAvatarLink());
        assertEquals(user.getUsername(), userEntity.getUsername());
        assertEquals(user.getCountry(), userEntity.getCountry());
        assertEquals(user.getRegion(), userEntity.getRegion());
        assertEquals(user.getTown(), userEntity.getTown());
        verify(fileManager).deleteOldAvatar("oldAvatar.png");
        verify(userDAO).update(userEntity);
    }

    @Test
    void editProfileUserNotFoundException() {
        //Given

        //When
        when(userDAO.findByLogin(user.getLogin())).thenReturn(Optional.empty());

        //Then
        assertThrows(UserNotFoundException.class,
                () -> userService.editProfile(user, multipartFile));

        verify(userDAO, never()).update(any());
    }
}