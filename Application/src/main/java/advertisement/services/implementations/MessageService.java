package advertisement.services.implementations;

import advertisement.daos.interfaces.IMessageDAO;
import advertisement.daos.interfaces.IUserDAO;
import advertisement.entities.MessageEntity;
import advertisement.entities.UserEntity;
import advertisement.mappers.IMessageModelToEntityMapper;
import advertisement.models.Message;
import advertisement.services.interfaces.IMessageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService implements IMessageService {
    @Autowired
    private IMessageDAO messageDAO;
    @Autowired
    private IMessageModelToEntityMapper messageModelToEntityMapper;
    @Autowired
    private IUserDAO userDAO;

    @Override
    @Transactional
    public List<Message> getChatMessages(String senderLogin, String recieverLogin) {
        Optional<UserEntity> optionalSender = userDAO.findByLogin(senderLogin);
        UserEntity sender = optionalSender.orElseThrow();
        Optional<UserEntity> optionalReciever = userDAO.findByLogin(recieverLogin);
        UserEntity reciever = optionalReciever.orElseThrow();
        return messageModelToEntityMapper.toModelList(messageDAO.getMessagesBySenderAndReciever(sender.getId(), reciever.getId()));
    }

    @Override
    @Transactional
    public Message sendMessage(Message model) {
        Optional<UserEntity> optionalSender = userDAO.findByLogin(model.getSender().getLogin());
        UserEntity sender = optionalSender.orElseThrow();
        Optional<UserEntity> optionalReciever = userDAO.findByLogin(model.getReciever().getLogin());
        UserEntity reciever = optionalReciever.orElseThrow();
        if (sender.getId() == reciever.getId()){
            throw new IllegalArgumentException("Нельзя отправить сообщения самому себе!");
        }

        Instant instant = Instant.now();
        MessageEntity messageEntity = messageModelToEntityMapper.toEntity(model);
        messageEntity.setSentAt(instant);
        messageEntity.setSender(sender);
        messageEntity.setReciever(reciever);

        messageDAO.save(messageEntity);

        return messageModelToEntityMapper.toModel(messageEntity);
    }
}
