package advertisement.daos.interfaces;

import advertisement.entities.RoleEntity;
import advertisement.entities.UserEntity;

import java.util.List;
import java.util.Optional;

public interface IUserDAO extends IGenericDAO<UserEntity, Long> {
    UserEntity save(UserEntity userEntity, List<RoleEntity> roles);
    Optional<UserEntity> findByLogin(String login);
    void update(UserEntity userEntity);
}
