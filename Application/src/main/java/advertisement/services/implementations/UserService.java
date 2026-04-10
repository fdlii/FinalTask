package advertisement.services.implementations;

import advertisement.daos.implementations.RoleDAO;
import advertisement.daos.implementations.UserDAO;
import advertisement.entities.RoleEntity;
import advertisement.files.interfaces.IFileManager;
import advertisement.mappers.IRoleModelToEntityMapper;
import advertisement.mappers.IUserModelToEntityMapper;
import advertisement.models.Role;
import advertisement.models.User;
import advertisement.services.interfaces.IUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements IUserService {
    @Autowired
    IRoleModelToEntityMapper roleMapper;
    @Autowired
    IUserModelToEntityMapper userMapper;
    @Autowired
    RoleDAO roleDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    IFileManager fileManager;

    @Override
    @Transactional
    public User registerUser(User user, MultipartFile multipartFile) throws IOException {
        List<RoleEntity> roles = roleDAO.findAll();
        List<RoleEntity> addedRoles = new ArrayList<>();
        for (String userRole : user.getRoles()) {
            boolean flag = false;
            for (RoleEntity role : roles) {
                if (userRole.equals(role.getName())) {
                    addedRoles.add(role);
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                throw new EntityNotFoundException("Неопознанная роль.");
            }
        }

        if (multipartFile != null) {
            String avatarUrl = fileManager.saveAvatar(multipartFile);
            user.setAvatarLink(avatarUrl);
        }
        userDAO.save(userMapper.toEntity(user), addedRoles);
        return user;
    }
}
