package advertisement.mappers;

import advertisement.DTOs.request.CommentRequestDTO;
import advertisement.DTOs.response.CommentResponseDTO;
import advertisement.models.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = IUserDTOToModelMapper.class)
public interface ICommentDTOToModelMapper {
    @Mapping(source = "adNumber", target = "advertisement.adNumber")
    @Mapping(source = "senderLogin", target = "user.login")
    Comment toModel(CommentRequestDTO commentRequestDTO);
    CommentResponseDTO toDTO(Comment comment);
    List<CommentResponseDTO> toDTOList(List<Comment> comments);
}
