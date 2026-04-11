package advertisement.mappers;

import advertisement.entities.UserEntity;
import advertisement.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IUserModelToEntityMapper {
    @Mapping(target = "roles", ignore = true)
    UserEntity toEntity(User user);
    @Mapping(target = "roles", ignore = true)
    User toModel(UserEntity userEntity);
}
