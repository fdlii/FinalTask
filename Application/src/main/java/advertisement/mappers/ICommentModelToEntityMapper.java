package advertisement.mappers;

import advertisement.entities.CommentEntity;
import advertisement.models.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {IDataMapper.class, IUserModelToEntityMapper.class, IAdvertisementModelToEntityMapper.class})
public interface ICommentModelToEntityMapper {
    @Mapping(target = "advertisement", ignore = true)
    @Mapping(source = "sentAt", target = "sentAt", qualifiedByName = "fromInstantToLocalDateTime")
    Comment toModel(CommentEntity commentEntity);
    @Mapping(target = "advertisement", ignore = true)
    @Mapping(source = "sentAt", target = "sentAt", qualifiedByName = "fromLocalDateTimeToInstant")
    CommentEntity toEntity(Comment comment);
    @Mapping(target = "advertisement", ignore = true)
    @Mapping(source = "sentAt", target = "sentAt", qualifiedByName = "fromInstantToLocalDateTime")
    List<Comment> toModelList(List<CommentEntity> commentEntity);
}
