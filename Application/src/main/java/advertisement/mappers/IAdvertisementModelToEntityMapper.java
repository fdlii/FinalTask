package advertisement.mappers;

import advertisement.entities.AdvertisementEntity;
import advertisement.models.Advertisement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {IDataMapper.class, IUserModelToEntityMapper.class, ICommentModelToEntityMapper.class, ICategoryModelToEntityMapper.class})
public interface IAdvertisementModelToEntityMapper {
    @Mapping(source = "id", target = "adNumber")
    @Mapping(source = "published", target = "published", qualifiedByName = "fromInstantToLocalDateTime")
    @Mapping(target = "comments", ignore = true)
    Advertisement toModel(AdvertisementEntity advertisementEntity);
    @Mapping(source = "adNumber", target = "id")
    @Mapping(source = "published", target = "published", qualifiedByName = "fromLocalDateTimeToInstant")
    AdvertisementEntity toEntity(Advertisement advertisement);
    @Mapping(source = "id", target = "adNumber")
    @Mapping(source = "published", target = "published", qualifiedByName = "fromInstantToLocalDateTime")
    @Mapping(target = "comments", ignore = true)
    List<Advertisement> toModelList(List<AdvertisementEntity> advertisementEntities);
}