package advertisement.services.implementations;

import advertisement.AdvertisementFilter;
import advertisement.daos.interfaces.IAdvertisementDAO;
import advertisement.daos.interfaces.ICategoryDAO;
import advertisement.daos.interfaces.IUserDAO;
import advertisement.entities.AdvertisementEntity;
import advertisement.entities.CategoryEntity;
import advertisement.entities.UserEntity;
import advertisement.exceptions.invalid.AdvertisementInvalidException;
import advertisement.exceptions.invalid.FilterInvalidException;
import advertisement.exceptions.notfound.AdvertisementNotFoundException;
import advertisement.exceptions.notfound.CategoryNotFoundException;
import advertisement.exceptions.notfound.UserNotFoundException;
import advertisement.exceptions.other.AdvertisementIllegalEditException;
import advertisement.files.interfaces.IFileManager;
import advertisement.mappers.IAdvertisementModelToEntityMapper;
import advertisement.models.Advertisement;
import advertisement.models.Category;
import advertisement.services.interfaces.IAdvertisementService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Service
public class AdvertisementService implements IAdvertisementService {
    Logger logger = LoggerFactory.getLogger(AdvertisementService.class);
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
        logger.info("Получение объявлений.");
        if (filter == null) {
            logger.error("Фильтр не указан.");
            throw new FilterInvalidException("Фильтр не указан.");
        }

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
                    logger.error("Категория {} не найдена.", name);
                    throw new CategoryNotFoundException("Категория " + name + " не найдена.");
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

        logger.info("Объявления успешно получены.");
        return advertisements;
    }

    @Override
    @Transactional
    public List<Advertisement> getSalesHistory(String login) {
        logger.info("Получение истории продаж.");
        Optional<UserEntity> optionalUserEntity = userDAO.findByLogin(login);
        UserEntity userEntity = optionalUserEntity.orElseThrow(() -> {
            logger.error("Пользователя с таким логином не существует.");
            throw new UserNotFoundException("Пользователя с таким логином не существует.");
        });

        List<AdvertisementEntity> advertisementEntities = advertisementDAO.findAdvertisementsByUserId(userEntity.getId());

        logger.info("История успешно получена.");
        return advertisementModelToEntityMapper
                .toModelList(advertisementEntities.stream().filter(AdvertisementEntity::isClosed).toList());
    }

    @Override
    @Transactional
    public Advertisement addAdvertisement(Advertisement advertisement, MultipartFile multipartFile) throws IOException {
        logger.info("Добавление объявления.");
        if (advertisement == null || advertisement.getUser() == null || advertisement.getTitle() == null) {
            logger.error("Объявление не указано либо не все обязательные поля заполнены.");
            throw new AdvertisementInvalidException("Объявление не указано либо не все обязательные поля заполнены.");
        }
        if (advertisement.getPrice() < 0) {
            logger.error("Цена товара/услуги не может быть меньше 0.");
            throw new AdvertisementInvalidException("Цена товара/услуги не может быть меньше 0.");
        }

        Optional<UserEntity> optionalUserEntity = userDAO.findByLogin(advertisement.getUser().getLogin());
        UserEntity userEntity = optionalUserEntity.orElseThrow(() -> {
            logger.error("Пользователя с таким логином не существует.");
            throw new UserNotFoundException("Пользователя с таким логином не существует.");
        });

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
                logger.error("Категория {} не найдена.", category.getName());
                throw new CategoryNotFoundException("Категория " + category.getName() + " не найдена.");
            }
        }

        Instant instant = Instant.now();
        advertisementEntity.setPublished(instant);
        advertisementEntity.setUser(userEntity);

        logger.info("Объявление успешно добавлено.");
        return advertisementModelToEntityMapper.toModel(advertisementDAO.save(advertisementEntity));
    }

    @Override
    @Transactional
    public Advertisement editAdvertisement(Advertisement model, MultipartFile multipartFile) throws IOException {
        logger.info("Редактирование объявления.");
        if (model == null || model.getUser() == null || model.getTitle() == null) {
            logger.error("Объявление не указано либо не все обязательные поля заполнены.");
            throw new AdvertisementInvalidException("Объявление не указано либо не все обязательные поля заполнены.");
        }
        if (model.getPrice() < 0) {
            logger.error("Цена товара/услуги не может быть меньше 0.");
            throw new AdvertisementInvalidException("Цена товара/услуги не может быть меньше 0.");
        }

        Optional<AdvertisementEntity> optionalAdvertisementEntity = advertisementDAO.findByAdNumber(model.getAdNumber());
        AdvertisementEntity advertisementEntity = optionalAdvertisementEntity.orElseThrow(() -> {
            logger.error("Объявления с таким артикулом не существует.");
            throw new AdvertisementNotFoundException("Объявления с таким артикулом не существует.");
        });
        advertisementEntity.setCategories(new ArrayList<>());

        List<Advertisement> advertisements = advertisementModelToEntityMapper
                .toModelList(advertisementDAO
                        .findAdvertisementsByUserId(advertisementEntity.getUser().getId()));

        boolean isOwner = advertisements.stream()
                .anyMatch(advertisement ->
                        Objects.equals(advertisement.getUser().getLogin(), model.getUser().getLogin()));

        if (!isOwner) {
            logger.error("Редактировать объявление может только его владелец.");
            throw new AdvertisementIllegalEditException("Редактировать объявление может только его владелец.");
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
                logger.error("Категория {} не найдена.", category.getName());
                throw new CategoryNotFoundException("Категория " + category.getName() + " не найдена.");
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

        logger.info("Объявление успешно отредактировано.");
        return advertisementModelToEntityMapper.toModel(advertisementEntity);
    }

    @Override
    @Transactional
    public Advertisement prepayAdvertisement(Advertisement model) {
        logger.info("Проплата объявления.");
        if (model == null) {
            logger.error("Объявление не указано.");
            throw new AdvertisementInvalidException("Объявление не указано.");
        }

        Optional<AdvertisementEntity> optionalAdvertisementEntity = advertisementDAO.findByAdNumber(model.getAdNumber());
        AdvertisementEntity advertisementEntity = optionalAdvertisementEntity.orElseThrow(() -> {
            logger.error("Объявления с таким артикулом не существует.");
            throw new AdvertisementNotFoundException("Объявления с таким артикулом не существует.");
        });
        advertisementEntity.setPaid(true);
        advertisementDAO.update(advertisementEntity);

        logger.info("Объявление успешно проплачено.");
        return advertisementModelToEntityMapper.toModel(advertisementEntity);
    }

    @Override
    @Transactional
    public Advertisement closeAdvertisement(Advertisement model) {
        logger.info("Закрытие объявления.");
        if (model == null || model.getUser() == null) {
            logger.error("Объявление не указано либо не все обязательные поля заполнены.");
            throw new AdvertisementInvalidException("Объявление не указано либо не все обязательные поля заполнены.");
        }

        Optional<AdvertisementEntity> optionalAdvertisementEntity = advertisementDAO.findByAdNumber(model.getAdNumber());
        AdvertisementEntity advertisementEntity = optionalAdvertisementEntity.orElseThrow(() -> {
            logger.error("Объявления с таким артикулом не существует.");
            throw new AdvertisementNotFoundException("Объявления с таким артикулом не существует.");
        });
        List<Advertisement> advertisements = advertisementModelToEntityMapper
                .toModelList(advertisementDAO
                        .findAdvertisementsByUserId(advertisementEntity.getUser().getId()));

        boolean isOwner = advertisements.stream()
                .anyMatch(advertisement ->
                        Objects.equals(advertisement.getUser().getLogin(), model.getUser().getLogin()));

        if (!isOwner) {
            logger.error("Редактировать объявление может только его владелец.");
            throw new AdvertisementIllegalEditException("Редактировать объявление может только его владелец.");
        }

        advertisementEntity.setClosed(true);
        advertisementDAO.update(advertisementEntity);

        logger.info("Объяаление успешно закрыто.");
        return advertisementModelToEntityMapper.toModel(advertisementEntity);
    }

    @Override
    @Transactional
    public Advertisement deleteAdvertisement(Advertisement model) throws IOException {
        logger.info("Удаление объявления.");
        if (model == null) {
            logger.error("Объявление не указано.");
            throw new AdvertisementInvalidException("Объявление не указано.");
        }

        Optional<AdvertisementEntity> optionalAdvertisementEntity = advertisementDAO.findByAdNumber(model.getAdNumber());
        AdvertisementEntity advertisementEntity = optionalAdvertisementEntity.orElseThrow(() -> {
            logger.error("Объявления с таким артикулом не существует.");
            throw new AdvertisementNotFoundException("Объявления с таким артикулом не существует.");
        });
        advertisementDAO.delete(advertisementEntity);
        fileManager.deleteOldPreview(advertisementEntity.getPreviewLink());

        logger.info("Объявление успешно удалено.");
        return advertisementModelToEntityMapper.toModel(advertisementEntity);
    }
}