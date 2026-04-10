package advertisement.daos.implementations;

import advertisement.daos.interfaces.IUserDAO;
import advertisement.entities.RoleEntity;
import advertisement.entities.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDAO extends GenericDAO<UserEntity, Long> implements IUserDAO {
    public UserDAO() {
        super(UserEntity.class);
    }

    @Override
    public UserEntity save(UserEntity userEntity, List<RoleEntity> roles) {
        userEntity.setRoles(roles);
        entityManager.persist(userEntity);
        return userEntity;
    }
}
