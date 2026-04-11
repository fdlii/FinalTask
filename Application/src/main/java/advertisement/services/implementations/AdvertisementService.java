package advertisement.services.implementations;

import advertisement.daos.interfaces.IAdvertisementDAO;
import advertisement.daos.interfaces.IUserDAO;
import advertisement.entities.AdvertisementEntity;
import advertisement.entities.UserEntity;
import advertisement.files.interfaces.IFileManager;
import advertisement.mappers.IAdvertisementModelToEntityMapper;
import advertisement.models.Advertisement;
import advertisement.services.interfaces.IAdvertisementService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AdvertisementService implements IAdvertisementService {
    @Autowired
    IFileManager fileManager;
    @Autowired
    IAdvertisementDAO advertisementDAO;
    @Autowired
    IAdvertisementModelToEntityMapper advertisementModelToEntityMapper;
    @Autowired
    IUserDAO userDAO;

    @Override
    @Transactional
    public Advertisement addAdvertisement(Advertisement advertisement, MultipartFile multipartFile) throws IOException {
        Optional<UserEntity> optionalUserEntity = userDAO.findByLogin(advertisement.getUser().getLogin());
        UserEntity userEntity = optionalUserEntity.orElseThrow();

        if (multipartFile != null) {
            String previewLink = fileManager.savePreview(multipartFile);
            advertisement.setPreviewLink(previewLink);
        }

        LocalDateTime published = LocalDateTime.now();
        advertisement.setPublished(published);

        AdvertisementEntity advertisementEntity = advertisementModelToEntityMapper.toEntity(advertisement);
        advertisementEntity.setUser(userEntity);

        return advertisementModelToEntityMapper.toModel(advertisementDAO.save(advertisementEntity));
    }
}
