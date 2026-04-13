package advertisement.mappers;

import advertisement.DTOs.request.AdvertisementRequestDTO;
import advertisement.DTOs.response.AdvertisementResponseDTO;
import advertisement.models.Advertisement;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {IUserDTOToModelMapper.class, ICategoryDTOToModelMapper.class})
public interface IAdvertisementDTOToModelMapper {
    AdvertisementResponseDTO toDTO(Advertisement advertisement);
    Advertisement toModel(AdvertisementRequestDTO advertisementRequestDTO);
    List<AdvertisementResponseDTO> toDTOList(List<Advertisement> advertisements);
}
