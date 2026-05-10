package advertisement.services;

import advertisement.daos.interfaces.IMessageDAO;
import advertisement.daos.interfaces.IUserDAO;
import advertisement.entities.MessageEntity;
import advertisement.entities.UserEntity;
import advertisement.exceptions.invalid.MessageInvalidException;
import advertisement.exceptions.notfound.UserNotFoundException;
import advertisement.mappers.IMessageModelToEntityMapper;
import advertisement.models.Message;
import advertisement.models.User;
import advertisement.services.implementations.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTestClass {
    @Mock
    private IMessageDAO messageDAO;
    @Mock
    private IMessageModelToEntityMapper messageModelToEntityMapper;
    @Mock
    private IUserDAO userDAO;

    @InjectMocks
    private MessageService messageService;

    private User sender;
    private User receiver;
    private UserEntity senderEntity;
    private UserEntity receiverEntity;
    private Message message;
    private MessageEntity messageEntity;

    @BeforeEach
    void setUp() {
        sender = User.builder().login("ngusev341@mail.ru").build();
        receiver = User.builder().login("ngusev342@mail.ru").build();

        senderEntity = UserEntity.builder().id(1L).login("ngusev341@mail.ru").build();
        receiverEntity = UserEntity.builder().id(2L).login("ngusev342@mail.ru").build();

        message = Message.builder()
                .sender(sender)
                .reciever(receiver)
                .content("Hello!")
                .build();

        messageEntity = MessageEntity.builder()
                .id(10L)
                .content("Hello!")
                .build();
    }

    // ---------- getChatMessages ----------

    @Test
    void getChatMessagesSuccessfully() {
        //Given
        List<MessageEntity> entities = List.of(messageEntity);
        List<Message> expected = List.of(message);

        //When
        when(userDAO.findByLogin("ngusev341@mail.ru")).thenReturn(Optional.of(senderEntity));
        when(userDAO.findByLogin("ngusev342@mail.ru")).thenReturn(Optional.of(receiverEntity));
        when(messageDAO.getMessagesBySenderAndReciever(1L, 2L)).thenReturn(entities);
        when(messageModelToEntityMapper.toModelList(entities)).thenReturn(expected);

        //Then
        List<Message> result = messageService.getChatMessages("ngusev341@mail.ru", "ngusev342@mail.ru");

        assertEquals(expected, result);
    }

    @Test
    void getChatMessagesUserNotFoundExceptionSender() {
        //Given

        //When
        when(userDAO.findByLogin("jdvsks@mail.ru")).thenReturn(Optional.empty());

        //Then
        assertThrows(UserNotFoundException.class,
                () -> messageService.getChatMessages("jdvsks@mail.ru", "ngusev342@mail.ru"));

        verify(messageDAO, never()).getMessagesBySenderAndReciever(anyLong(), anyLong());
    }

    @Test
    void getChatMessagesUserNotFoundExceptionReciever() {
        //Given

        //When
        when(userDAO.findByLogin("ngusev341@mail.ru")).thenReturn(Optional.of(senderEntity));
        when(userDAO.findByLogin("jdvsks@mail.ru")).thenReturn(Optional.empty());

        //Then
        assertThrows(UserNotFoundException.class,
                () -> messageService.getChatMessages("ngusev341@mail.ru", "jdvsks@mail.ru"));

        verify(messageDAO, never()).getMessagesBySenderAndReciever(anyLong(), anyLong());
    }

    // ---------- sendMessage ----------

    @Test
    void sendMessageSuccessfully() {
        //Given

        //When
        when(userDAO.findByLogin("ngusev341@mail.ru")).thenReturn(Optional.of(senderEntity));
        when(userDAO.findByLogin("ngusev342@mail.ru")).thenReturn(Optional.of(receiverEntity));
        when(messageModelToEntityMapper.toEntity(message)).thenReturn(messageEntity);
        when(messageModelToEntityMapper.toModel(messageEntity)).thenReturn(message);

        //Then
        Message result = messageService.sendMessage(message);

        assertEquals(message, result);
        assertEquals(senderEntity, messageEntity.getSender());
        assertEquals(receiverEntity, messageEntity.getReciever());
        assertNotNull(messageEntity.getSentAt());
        verify(messageDAO).save(messageEntity);
    }

    @Test
    void sendMessageUserNotFoundExceptionSender() {
        //Given
        sender.setLogin("jdvsks@mail.ru");

        //When
        when(userDAO.findByLogin("jdvsks@mail.ru")).thenReturn(Optional.empty());

        //Then
        assertThrows(UserNotFoundException.class,
                () -> messageService.sendMessage(message));

        verify(messageDAO, never()).save(any());
    }

    @Test
    void sendMessageUserNotFoundExceptionReciever() {
        //Given
        receiver.setLogin("jdvsks@mail.ru");

        //When
        when(userDAO.findByLogin("ngusev341@mail.ru")).thenReturn(Optional.of(senderEntity));
        when(userDAO.findByLogin("jdvsks@mail.ru")).thenReturn(Optional.empty());

        //Then
        assertThrows(UserNotFoundException.class,
                () -> messageService.sendMessage(message));

        verify(messageDAO, never()).save(any());
    }

    @Test
    void sendMessageMessageInvalidException() {
        //Given
        Message selfMessage = Message.builder()
                .sender(sender)
                .reciever(sender)
                .content("Hello me!")
                .build();

        //When
        when(userDAO.findByLogin("ngusev341@mail.ru")).thenReturn(Optional.of(senderEntity));

        //Then
        assertThrows(MessageInvalidException.class,
                () -> messageService.sendMessage(selfMessage));

        verify(messageDAO, never()).save(any());
    }
}