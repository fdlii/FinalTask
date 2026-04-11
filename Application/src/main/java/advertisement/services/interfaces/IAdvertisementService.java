package advertisement.services.interfaces;

import advertisement.models.Advertisement;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IAdvertisementService {
    Advertisement addAdvertisement(Advertisement advertisement, MultipartFile multipartFile) throws IOException;
}
