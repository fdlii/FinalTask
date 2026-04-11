package advertisement.mappers;

import advertisement.DTOs.request.UserRequestDTO;
import advertisement.DTOs.response.UserResponseDTO;
import advertisement.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IUserDTOToModelMapper {
    User toUser(UserRequestDTO userRequestDTO);
    @Mapping(source = "avatarLink", target = "avatarUrl")
    UserResponseDTO toDTO(User user);
}
