package advertisement.services.implementations;

import advertisement.AdvertisementFilter;
import advertisement.daos.interfaces.IAdvertisementDAO;
import advertisement.daos.interfaces.ICategoryDAO;
import advertisement.daos.interfaces.IUserDAO;
import advertisement.entities.AdvertisementEntity;
import advertisement.entities.CategoryEntity;
import advertisement.entities.UserEntity;
import advertisement.files.interfaces.IFileManager;
import advertisement.mappers.IAdvertisementModelToEntityMapper;
import advertisement.models.Advertisement;
import advertisement.models.Category;
import advertisement.services.interfaces.IAdvertisementService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdvertisementService implements IAdvertisementService {
    @Autowired
    private IFileManager fileManager;
    @Autowired
    private IAdvertisementDAO advertisementDAO;
    @Autowired
    private IAdvertisementModelToEntityMapper advertisementModelToEntityMapper;
    @Autowired
    private IUserDAO userDAO;
    @Autowired
    private ICategoryDAO categoryDAO;

    @Override
    @Transactional
    public List<Advertisement> getAdvertisements(AdvertisementFilter filter) {
        List<Long> categoriesIds = new ArrayList<>();

        if (filter.getCategories() != null) {
            List<CategoryEntity> categoryEntities = categoryDAO.findAll();
            for (String name : filter.getCategories()) {
                boolean flag = false;
                for (CategoryEntity categoryEntity : categoryEntities) {
                    if (name.equals(categoryEntity.getName())) {
                        flag = true;
                        categoriesIds.add(categoryEntity.getId());
                        break;
                    }
                }
                if (!flag) {
                    throw new NoSuchElementException("Указанная категория не найдена.");
                }
            }
        }

        List<Advertisement> advertisements = advertisementModelToEntityMapper
                .toModelList(advertisementDAO
                        .findWithFilter(filter.getTitle(), filter.getTown(), categoriesIds));
        advertisements.sort(Comparator
                .comparing(Advertisement::isClosed)
                .thenComparing(Advertisement::isPaid, Comparator.reverseOrder())
                .thenComparing(advertisement -> advertisement.getUser().getSellerRating(), Comparator.reverseOrder()));
        return advertisements;
    }

    @Override
    @Transactional
    public List<Advertisement> getSalesHistory(String login) {
        Optional<UserEntity> optionalUserEntity = userDAO.findByLogin(login);
        UserEntity userEntity = optionalUserEntity.orElseThrow();

        List<AdvertisementEntity> advertisementEntities = advertisementDAO.findAdvertisementsByUserId(userEntity.getId());
        return advertisementModelToEntityMapper
                .toModelList(advertisementEntities.stream().filter(AdvertisementEntity::isClosed).toList());
    }

    @Override
    @Transactional
    public Advertisement addAdvertisement(Advertisement advertisement, MultipartFile multipartFile) throws IOException {
        Optional<UserEntity> optionalUserEntity = userDAO.findByLogin(advertisement.getUser().getLogin());
        UserEntity userEntity = optionalUserEntity.orElseThrow();

        if (multipartFile != null) {
            String previewLink = fileManager.savePreview(multipartFile);
            advertisement.setPreviewLink(previewLink);
        }

        AdvertisementEntity advertisementEntity = advertisementModelToEntityMapper.toEntity(advertisement);

        List<CategoryEntity> categoryEntities = categoryDAO.findAll();
        for (Category category : advertisement.getCategories()) {
            boolean flag = false;
            for (CategoryEntity categoryEntity : categoryEntities) {
                if (category.getName().equals(categoryEntity.getName())) {
                    flag = true;
                    advertisementEntity.addCategory(categoryEntity);
                    break;
                }
            }
            if (!flag) {
                throw new NoSuchElementException("Указанная категория не найдена.");
            }
        }

        Instant instant = Instant.now();
        advertisementEntity.setPublished(instant);
        advertisementEntity.setUser(userEntity);

        return advertisementModelToEntityMapper.toModel(advertisementDAO.save(advertisementEntity));
    }

    @Override
    @Transactional
    public Advertisement editAdvertisement(Advertisement model, MultipartFile multipartFile) throws IOException, IllegalAccessException {
        Optional<AdvertisementEntity> optionalAdvertisementEntity = advertisementDAO.findByAdNumber(model.getAdNumber());
        AdvertisementEntity advertisementEntity = optionalAdvertisementEntity.orElseThrow();
        advertisementEntity.setCategories(new ArrayList<>());

        List<Advertisement> advertisements = advertisementModelToEntityMapper
                .toModelList(advertisementDAO
                        .findAdvertisementsByUserId(advertisementEntity.getUser().getId()));

        boolean isOwner = advertisements.stream()
                .anyMatch(advertisement ->
                        Objects.equals(advertisement.getUser().getLogin(), model.getUser().getLogin()));

        if (!isOwner) {
            throw new IllegalAccessException("Редактировать объявление может только его владелец.");
        }

        List<CategoryEntity> categoryEntities = categoryDAO.findAll();

        for (Category category : model.getCategories()) {
            boolean flag = false;
            for (CategoryEntity categoryEntity : categoryEntities) {
                if (category.getName().equals(categoryEntity.getName())) {
                    flag = true;
                    advertisementEntity.addCategory(categoryEntity);
                    break;
                }
            }
            if (!flag) {
                throw new NoSuchElementException("Указанная категория не найдена.");
            }
        }

        if (multipartFile != null) {
            fileManager.deleteOldPreview(advertisementEntity.getPreviewLink());
            String newPreviewLink = fileManager.savePreview(multipartFile);
            advertisementEntity.setPreviewLink(newPreviewLink);
        }

        advertisementEntity.setTitle(model.getTitle());
        advertisementEntity.setDescription(model.getDescription());
        advertisementEntity.setPrice(model.getPrice());
        advertisementEntity.setCountry(model.getCountry());
        advertisementEntity.setRegion(model.getRegion());
        advertisementEntity.setTown(model.getTown());

        advertisementDAO.update(advertisementEntity);
        return advertisementModelToEntityMapper.toModel(advertisementEntity);
    }

    @Override
    @Transactional
    public Advertisement prepayAdvertisement(Advertisement model) {
        Optional<AdvertisementEntity> optionalAdvertisementEntity = advertisementDAO.findByAdNumber(model.getAdNumber());
        AdvertisementEntity advertisementEntity = optionalAdvertisementEntity.orElseThrow();
        advertisementEntity.setPaid(true);
        advertisementDAO.update(advertisementEntity);
        return advertisementModelToEntityMapper.toModel(advertisementEntity);
    }

    @Override
    @Transactional
    public Advertisement closeAdvertisement(Advertisement model) throws IllegalAccessException {
        Optional<AdvertisementEntity> optionalAdvertisementEntity = advertisementDAO.findByAdNumber(model.getAdNumber());
        AdvertisementEntity advertisementEntity = optionalAdvertisementEntity.orElseThrow();
        List<Advertisement> advertisements = advertisementModelToEntityMapper
                .toModelList(advertisementDAO
                        .findAdvertisementsByUserId(advertisementEntity.getUser().getId()));

        boolean isOwner = advertisements.stream()
                .anyMatch(advertisement ->
                        Objects.equals(advertisement.getUser().getLogin(), model.getUser().getLogin()));

        if (!isOwner) {
            throw new IllegalAccessException("Закрыть объявление может только его владелец.");
        }

        advertisementEntity.setClosed(true);
        advertisementDAO.update(advertisementEntity);
        return advertisementModelToEntityMapper.toModel(advertisementEntity);
    }

    @Override
    @Transactional
    public Advertisement deleteAdvertisement(Advertisement model) throws IOException {
        Optional<AdvertisementEntity> optionalAdvertisementEntity = advertisementDAO.findByAdNumber(model.getAdNumber());
        AdvertisementEntity advertisementEntity = optionalAdvertisementEntity.orElseThrow();
        advertisementDAO.delete(advertisementEntity);
        fileManager.deleteOldPreview(advertisementEntity.getPreviewLink());
        return advertisementModelToEntityMapper.toModel(advertisementEntity);
    }

}
