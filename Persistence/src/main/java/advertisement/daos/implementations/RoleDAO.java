package advertisement.daos.implementations;

import advertisement.daos.interfaces.IRoleDAO;
import advertisement.entities.RoleEntity;
import org.springframework.stereotype.Repository;

@Repository
public class RoleDAO extends GenericDAO<RoleEntity, Long> implements IRoleDAO {
    public RoleDAO() {
        super(RoleEntity.class);
    }
}
