package advertisement.services.implementations;

import advertisement.daos.interfaces.IMessageDAO;
import advertisement.daos.interfaces.IUserDAO;
import advertisement.entities.MessageEntity;
import advertisement.entities.UserEntity;
import advertisement.exceptions.invalid.MessageInvalidException;
import advertisement.exceptions.notfound.UserNotFoundException;
import advertisement.mappers.IMessageModelToEntityMapper;
import advertisement.models.Message;
import advertisement.services.interfaces.IMessageService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService implements IMessageService {
    Logger logger = LoggerFactory.getLogger(MessageService.class);
    @Autowired
    private IMessageDAO messageDAO;
    @Autowired
    private IMessageModelToEntityMapper messageModelToEntityMapper;
    @Autowired
    private IUserDAO userDAO;

    @Override
    @Transactional
    public List<Message> getChatMessages(String senderLogin, String recieverLogin) {
        logger.info("Получение сообщений чата.");
        Optional<UserEntity> optionalSender = userDAO.findByLogin(senderLogin);
        UserEntity sender = optionalSender.orElseThrow(() -> {
            logger.error("Отправителя с таким логином не существует.");
            throw new UserNotFoundException("Отправителя с таким логином не существует.");
        });
        Optional<UserEntity> optionalReciever = userDAO.findByLogin(recieverLogin);
        UserEntity reciever = optionalReciever.orElseThrow(() -> {
            logger.error("Получателя с таким логином не существует.");
            throw new UserNotFoundException("Получателя с таким логином не существует.");
        });

        logger.info("Сообщения успешно получены.");
        return messageModelToEntityMapper.toModelList(messageDAO.getMessagesBySenderAndReciever(sender.getId(), reciever.getId()));
    }

    @Override
    @Transactional
    public Message sendMessage(Message model) {
        logger.info("Отправка сообщения.");
        if (model == null || model.getSender() == null || model.getReciever() == null || model.getContent() == null || model.getContent().isEmpty()) {
            logger.error("Сообщение не указано либо не все обязательные параметры заданы.");
            throw new MessageInvalidException("Сообщение не указано либо не все обязательные параметры заданы.");
        }

        Optional<UserEntity> optionalSender = userDAO.findByLogin(model.getSender().getLogin());
        UserEntity sender = optionalSender.orElseThrow(() -> {
            logger.error("Отправителя с таким логином не существует.");
            throw new UserNotFoundException("Отправителя с таким логином не существует.");
        });
        Optional<UserEntity> optionalReciever = userDAO.findByLogin(model.getReciever().getLogin());
        UserEntity reciever = optionalReciever.orElseThrow(() -> {
            logger.error("Получателя с таким логином не существует.");
            throw new UserNotFoundException("Получателя с таким логином не существует.");
        });
        if (sender.getId() == reciever.getId()) {
            logger.error("Нельзя отправить сообщения самому себе!");
            throw new MessageInvalidException("Нельзя отправить сообщения самому себе!");
        }

        Instant instant = Instant.now();
        MessageEntity messageEntity = messageModelToEntityMapper.toEntity(model);
        messageEntity.setSentAt(instant);
        messageEntity.setSender(sender);
        messageEntity.setReciever(reciever);

        messageDAO.save(messageEntity);

        logger.info("Сообщение успешно отправлено.");
        return messageModelToEntityMapper.toModel(messageEntity);
    }
}
