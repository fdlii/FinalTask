package advertisement.mappers;

import advertisement.entities.RatingEntity;
import advertisement.models.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {IDataMapper.class, IUserModelToEntityMapper.class})
public interface IRatingModelToEntityMapper {
    @Mapping(source = "writtenAt", target = "writtenAt", qualifiedByName = "fromLocalDateTimeToInstant")
    RatingEntity toEntity(Rating rating);
    @Mapping(source = "writtenAt", target = "writtenAt", qualifiedByName = "fromInstantToLocalDateTime")
    Rating toModel(RatingEntity ratingEntity);
    @Mapping(source = "writtenAt", target = "writtenAt", qualifiedByName = "fromInstantToLocalDateTime")
    List<Rating> toModelList(List<RatingEntity> ratingEntities);
}
