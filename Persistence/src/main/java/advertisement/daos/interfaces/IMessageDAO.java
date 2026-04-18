package advertisement.daos.interfaces;

import advertisement.entities.MessageEntity;

import java.util.List;

public interface IMessageDAO extends IGenericDAO<MessageEntity> {
    List<MessageEntity> getMessagesBySenderAndReciever(Long senderId, Long recieverId);
}
