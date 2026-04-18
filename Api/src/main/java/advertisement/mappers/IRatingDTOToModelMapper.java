package advertisement.mappers;

import advertisement.DTOs.request.RatingRequestDTO;
import advertisement.DTOs.response.RatingResponseDTO;
import advertisement.models.Rating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {IUserDTOToModelMapper.class})
public interface IRatingDTOToModelMapper {
    RatingResponseDTO toDTO(Rating rating);
    List<RatingResponseDTO> toDTOList(List<Rating> ratings);
    @Mapping(source = "sellerLogin", target = "seller.login")
    @Mapping(source = "reviewerLogin", target = "reviewer.login")
    Rating toModel(RatingRequestDTO ratingRequestDTO);
}
