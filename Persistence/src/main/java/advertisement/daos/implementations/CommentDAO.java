package advertisement.daos.implementations;

import advertisement.daos.interfaces.ICommentDAO;
import advertisement.entities.CommentEntity;
import org.springframework.stereotype.Repository;

@Repository
public class CommentDAO extends GenericDAO<CommentEntity> implements ICommentDAO {
    public CommentDAO() {
        super(CommentEntity.class);
    }
}
