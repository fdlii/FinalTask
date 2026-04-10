package advertisement.daos.interfaces;

import advertisement.entities.RoleEntity;
import advertisement.entities.UserEntity;

import java.util.List;

public interface IUserDAO {
    UserEntity save(UserEntity userEntity, List<RoleEntity> roles);
}
