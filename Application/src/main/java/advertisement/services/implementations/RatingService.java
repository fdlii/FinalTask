package advertisement.services.implementations;

import advertisement.daos.interfaces.IRatingDAO;
import advertisement.daos.interfaces.IUserDAO;
import advertisement.entities.RatingEntity;
import advertisement.entities.UserEntity;
import advertisement.exceptions.invalid.RatingInvalidException;
import advertisement.exceptions.notfound.UserNotFoundException;
import advertisement.mappers.IRatingModelToEntityMapper;
import advertisement.models.Rating;
import advertisement.services.interfaces.IRatingService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class RatingService implements IRatingService {
    Logger logger = LoggerFactory.getLogger(RatingService.class);
    @Autowired
    private IRatingDAO ratingDAO;
    @Autowired
    private IUserDAO userDAO;
    @Autowired
    private IRatingModelToEntityMapper ratingModelToEntityMapper;

    @Override
    @Transactional
    public List<Rating> getSellerRatings(String login) {
        logger.info("Получение оценок продавца.");
        Optional<UserEntity> optionalSeller = userDAO.findByLogin(login);
        UserEntity seller = optionalSeller.orElseThrow(() -> {
            logger.error("Пользователя с таким логином не существует.");
            throw new UserNotFoundException("Пользователя с таким логином не существует.");
        });

        logger.info("Оценки успешно получены.");
        return ratingModelToEntityMapper.toModelList(ratingDAO.getSellerRatings(seller.getId()));
    }

    @Override
    @Transactional
    public Rating addRating(Rating rating) {
        logger.info("Добавление отзыва.");
        if (rating == null || rating.getSeller() == null || rating.getReviewer() == null) {
            logger.error("Рейтинг не указан либо не все обязательные поля заполнены.");
            throw new RatingInvalidException("Рейтинг не указан либо не все обязательные поля заполнены.");
        }
        if (rating.getScore() < 1 || rating.getScore() > 5) {
            logger.error("Оценка должна быть от 1 до 5.");
            throw new RatingInvalidException("Оценка должна быть от 1 до 5.");
        }

        Optional<UserEntity> optionalSeller = userDAO.findByLogin(rating.getSeller().getLogin());
        UserEntity seller = optionalSeller.orElseThrow(() -> {
            logger.error("Продавца с таким логином не существует.");
            throw new UserNotFoundException("Продавца с таким логином не существует.");
        });
        Optional<UserEntity> optionalReviewer = userDAO.findByLogin(rating.getReviewer().getLogin());
        UserEntity reviewer = optionalReviewer.orElseThrow(() -> {
            logger.error("Клиента с таким логином не существует.");
            throw new UserNotFoundException("Клиента с таким логином не существует.");
        });
        if (seller.getId() == reviewer.getId()) {
            logger.error("Нельзя поставить оценку самому себе!");
            throw new RatingInvalidException("Нельзя поставить оценку самому себе!");
        }

        Instant instant = Instant.now();
        RatingEntity ratingEntity = ratingModelToEntityMapper.toEntity(rating);
        ratingEntity.setWrittenAt(instant);
        ratingEntity.setSeller(seller);
        ratingEntity.setReviewer(reviewer);

        ratingDAO.save(ratingEntity);

        double score = ratingDAO.getSellerRating(seller.getId());
        ratingEntity.getSeller().setSellerRating(score);

        logger.info("Оценка успешно добавлена.");
        return ratingModelToEntityMapper.toModel(ratingEntity);
    }
}