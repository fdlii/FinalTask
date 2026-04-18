package advertisement.mappers;

import advertisement.entities.MessageEntity;
import advertisement.models.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {IDataMapper.class, IUserModelToEntityMapper.class})
public interface IMessageModelToEntityMapper {
    @Mapping(source = "sentAt", target = "sentAt", qualifiedByName = "fromInstantToLocalDateTime")
    Message toModel(MessageEntity messageEntity);
    @Mapping(source = "sentAt", target = "sentAt", qualifiedByName = "fromInstantToLocalDateTime")
    List<Message> toModelList(List<MessageEntity> messageEntities);
    @Mapping(source = "sentAt", target = "sentAt", qualifiedByName = "fromLocalDateTimeToInstant")
    MessageEntity toEntity(Message message);
}
