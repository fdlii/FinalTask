package advertisement.services.implementations;

import advertisement.AdminConfig;
import advertisement.daos.implementations.RoleDAO;
import advertisement.daos.implementations.UserDAO;
import advertisement.entities.RoleEntity;
import advertisement.entities.UserEntity;
import advertisement.exceptions.notfound.RoleNotFoundException;
import advertisement.exceptions.other.UserAlreadyExistException;
import advertisement.exceptions.invalid.UserInvalidException;
import advertisement.exceptions.notfound.UserNotFoundException;
import advertisement.files.interfaces.IFileManager;
import advertisement.mappers.IUserModelToEntityMapper;
import advertisement.models.User;
import advertisement.services.interfaces.IUserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {
    Logger logger = LoggerFactory.getLogger(UserService.class);
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
        logger.info("Регистрация пользователя.");
        if (user == null || user.getLogin() == null || user.getPassword() == null || user.getUsername() == null || user.getRoles() == null) {
            logger.error("Пользователь не указан либо не все обязательные поля заполнены.");
            throw new UserInvalidException("Пользователь не указан либо не все обязательные поля заполнены.");
        }

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
        userDAO.save(userMapper.toEntity(user), addedRoles);

        logger.info("Регистрация успешна.");
        return user;
    }

    @Override
    @Transactional
    public User changePassword(User user) {
        logger.info("Смена пароля пользователя.");
        if (user == null || user.getPassword() == null) {
            logger.error("Пользователь не указан.");
            throw new UserInvalidException("Пользователь не указан.");
        }

        Optional<UserEntity> optionalUserEntity = userDAO.findByLogin(user.getLogin());
        UserEntity userEntity = optionalUserEntity.orElseThrow(() -> {
            logger.error("Пользователя с таким логином не существует.");
            throw new UserNotFoundException("Пользователя с таким логином не существует.");
        });
        userEntity.setPassword(user.getPassword());
        userDAO.update(userEntity);

        logger.info("Пароль успешно изменён.");
        return user;
    }

    @Override
    @Transactional
    public User editProfile(User user, MultipartFile avatar) throws IOException {
        logger.info("Редактирование профиля.");
        if (user == null || user.getLogin() == null || user.getPassword() == null || user.getUsername() == null || user.getRoles() == null) {
            logger.error("Пользователь не указан либо не все обязательные поля заполнены.");
            throw new UserInvalidException("Пользователь не указан либо не все обязательные поля заполнены.");
        }

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