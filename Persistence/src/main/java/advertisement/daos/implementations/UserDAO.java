package advertisement.daos.implementations;

import advertisement.daos.interfaces.IUserDAO;
import advertisement.entities.RoleEntity;
import advertisement.entities.UserEntity;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDAO extends GenericDAO<UserEntity> implements IUserDAO {
    public UserDAO() {
        super(UserEntity.class);
    }

    @Override
    public UserEntity save(UserEntity userEntity, List<RoleEntity> roles) {
        userEntity.setRoles(roles);
        entityManager.persist(userEntity);
        return userEntity;
    }

    @Override
    public Optional<UserEntity> findByLogin(String login) {
        String jpql = "SELECT u FROM UserEntity u WHERE u.login = :login";
        TypedQuery<UserEntity> query = entityManager.createQuery(jpql, UserEntity.class);
        query.setParameter("login", login);

        return query.getResultList().stream().findFirst();
    }
}
