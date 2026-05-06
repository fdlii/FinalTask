package advertisement.services.implementations;

import advertisement.AdminConfig;
import advertisement.JwtHandler;
import advertisement.daos.implementations.RoleDAO;
import advertisement.daos.implementations.UserDAO;
import advertisement.entities.RoleEntity;
import advertisement.entities.UserEntity;
import advertisement.exceptions.notfound.RoleNotFoundException;
import advertisement.exceptions.other.UserAlreadyExistException;
import advertisement.exceptions.notfound.UserNotFoundException;
import advertisement.files.interfaces.IFileManager;
import advertisement.mappers.IUserModelToEntityMapper;
import advertisement.models.User;
import advertisement.security.UserPrincipal;
import advertisement.services.interfaces.IUserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(8);
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtHandler jwtHandler;
    @Autowired
    AdminConfig adminConfig;
    @Autowired
    private IUserModelToEntityMapper userMapper;
    @Autowired
    private RoleDAO roleDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private IFileManager fileManager;

    @Override
    @Transactional
    public User registerUser(User user, MultipartFile multipartFile) throws IOException, IllegalAccessException {
        Optional<UserEntity> optionalUserEntity = userDAO.findByLogin(user.getLogin());
        if (optionalUserEntity.isPresent()) {
            logger.error("Пользователь с таким логином уже существует.");
            throw new UserAlreadyExistException("Пользователь с таким логином уже существует.");
        }

        List<RoleEntity> roles = roleDAO.findAll();
        List<RoleEntity> addedRoles = new ArrayList<>();
        for (String userRole : user.getRoles()) {
            boolean flag = false;
            for (RoleEntity role : roles) {
                if (userRole.equals(role.getName())) {
                    if (userRole.equals("ROLE_ADMIN") && !user.getSecretAdminKey().equals(adminConfig.getApiKey())) {
                        logger.error("Неверный пароль администратора.");
                        throw new IllegalAccessException("Неверный пароль администратора.");
                    }
                    addedRoles.add(role);
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                logger.error("Неизвестное имя роли.");
                throw new RoleNotFoundException("Неизвестное имя роли: " + userRole);
            }
        }

        if (multipartFile != null) {
            String avatarUrl = fileManager.saveAvatar(multipartFile);
            user.setAvatarLink(avatarUrl);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userDAO.save(userMapper.toEntity(user), addedRoles);

        logger.info("Регистрация успешна.");
        return user;
    }

    @Override
    @Transactional
    public String verifyUser(String login, String password) {
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login, password)
            );
        } catch (AuthenticationException e) {
            throw new AuthenticationException("Неверный пароль для входа.") {
            };
        }

        if (authentication.isAuthenticated()) {
            UserPrincipal userPrincipal = new UserPrincipal(userDAO.findByLogin(login).get());
            logger.info("Пользователь успешно вошёл.");
            return jwtHandler.generateToken(login, userPrincipal.getAuthorities());
        }
        return null;
    }

    @Override
    @Transactional
    public void changePassword(String login, String password) {
        Optional<UserEntity> optionalUserEntity = userDAO.findByLogin(login);
        UserEntity userEntity = optionalUserEntity.orElseThrow(() -> {
            logger.error("Пользователя с таким логином не существует.");
            throw new UserNotFoundException("Пользователя с таким логином не существует.");
        });
        userEntity.setPassword(passwordEncoder.encode(password));
        userDAO.update(userEntity);

        logger.info("Пароль успешно изменён.");
    }

    @Override
    @Transactional
    public User editProfile(User user, MultipartFile avatar) throws IOException {
        Optional<UserEntity> optionalUserEntity = userDAO.findByLogin(user.getLogin());
        UserEntity userEntity = optionalUserEntity.orElseThrow(() -> {
            logger.error("Пользователя с таким логином не существует.");
            throw new UserNotFoundException("Пользователя с таким логином не существует.");
        });

        fileManager.deleteOldAvatar(userEntity.getAvatarLink());
        String newAvatarLink = fileManager.saveAvatar(avatar);

        userEntity.setUsername(user.getUsername());
        userEntity.setCountry(user.getCountry());
        userEntity.setRegion(user.getRegion());
        userEntity.setTown(user.getTown());
        userEntity.setAvatarLink(newAvatarLink);
        userDAO.update(userEntity);

        user.setAvatarLink(newAvatarLink);

        logger.info("Профиль успешно отредактирован.");
        return user;
    }
}