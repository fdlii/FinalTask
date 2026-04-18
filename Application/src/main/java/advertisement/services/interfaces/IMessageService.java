package advertisement.services.interfaces;

import advertisement.models.Message;

import java.util.List;

public interface IMessageService {
    Message sendMessage(Message model);
    List<Message> getChatMessages(String senderLogin, String recieverLogin);
}
