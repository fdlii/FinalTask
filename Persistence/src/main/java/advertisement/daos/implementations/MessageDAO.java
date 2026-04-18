package advertisement.daos.implementations;

import advertisement.daos.interfaces.IMessageDAO;
import advertisement.entities.MessageEntity;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MessageDAO extends GenericDAO<MessageEntity> implements IMessageDAO {
    public MessageDAO() {
        super(MessageEntity.class);
    }

    @Override
    public List<MessageEntity> getMessagesBySenderAndReciever(Long senderId, Long recieverId) {
        String jpql = "SELECT m FROM MessageEntity m " +
                "WHERE m.sender.id = :senderId AND m.reciever.id = :recieverId OR " +
                "m.sender.id = :recieverId AND m.reciever.id = :senderId";
        TypedQuery<MessageEntity> query = entityManager.createQuery(jpql, MessageEntity.class);

        query.setParameter("senderId", senderId);
        query.setParameter("recieverId", recieverId);

        return query.getResultList();
    }
}
