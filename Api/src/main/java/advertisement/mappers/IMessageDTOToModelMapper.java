package advertisement.mappers;

import advertisement.DTOs.request.MessageRequestDTO;
import advertisement.DTOs.response.MessageResponseDTO;
import advertisement.models.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IMessageDTOToModelMapper {
    @Mapping(source = "senderLogin", target = "sender.login")
    @Mapping(source = "recieverLogin", target = "reciever.login")
    Message toModel(MessageRequestDTO messageRequestDTO);
    @Mapping(source = "sender.login", target = "senderLogin")
    @Mapping(source = "reciever.login", target = "recieverLogin")
    MessageResponseDTO toDTO(Message message);
    @Mapping(source = "sender.login", target = "senderLogin")
    @Mapping(source = "reciever.login", target = "recieverLogin")
    List<MessageResponseDTO> toDTOList(List<Message> messages);
}
