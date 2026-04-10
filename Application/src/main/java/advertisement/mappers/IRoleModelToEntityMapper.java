package advertisement.mappers;

import advertisement.entities.RoleEntity;
import advertisement.models.Role;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IRoleModelToEntityMapper {
    RoleEntity toEntity(Role role);
    Role toModel(RoleEntity roleEntity);
    List<Role> toModelList(List<RoleEntity> roleEntities);
    List<RoleEntity> toEntityList(List<Role> roles);
}
