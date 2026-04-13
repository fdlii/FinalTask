package advertisement.services.interfaces;

import advertisement.models.Advertisement;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IAdvertisementService {
    Advertisement addAdvertisement(Advertisement advertisement, MultipartFile multipartFile) throws IOException;
    List<Advertisement> getAllAdvertisements();
    List<Advertisement> getSalesHistory(String login);
    Advertisement deleteAdvertisement(Advertisement model) throws IOException;
    Advertisement prepayAdvertisement(Advertisement model);
    Advertisement editAdvertisement(Advertisement model, MultipartFile multipartFile) throws IOException, IllegalAccessException;
    Advertisement closeAdvertisement(Advertisement model) throws IllegalAccessException;
}
