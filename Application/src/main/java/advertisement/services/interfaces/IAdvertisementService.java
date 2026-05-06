package advertisement.services.interfaces;

import advertisement.AdvertisementFilter;
import advertisement.models.Advertisement;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IAdvertisementService {
    Advertisement addAdvertisement(Advertisement advertisement, MultipartFile multipartFile) throws IOException;
    List<Advertisement> getAdvertisements(AdvertisementFilter filter);
    List<Advertisement> getSalesHistory(String login);
    void deleteAdvertisement(Long adNumber) throws IOException;
    void prepayAdvertisement(Long adNumber);
    Advertisement editAdvertisement(Advertisement model, MultipartFile multipartFile) throws IOException, IllegalAccessException;
    void closeAdvertisement(Long adNumber, String login) throws IllegalAccessException;
}
