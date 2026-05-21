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
    private static final Logger logger = LoggerFactory.getLogger(RatingService.class);
    @Autowired
    private IRatingDAO ratingDAO;
    @Autowired
    private IUserDAO userDAO;
    @Autowired
    private IRatingModelToEntityMapper ratingModelToEntityMapper;

    @Override
    @Transactional
    public List<Rating> getSellerRatings(String login) {
        Optional<UserEntity> optionalSeller = userDAO.findByLogin(login);
        UserEntity seller = optionalSeller.orElseThrow(() -> {
            logger.error("Продавца с логином {} не существует.", login);
            throw new UserNotFoundException("Продавца с логином " + login + " не существует.");
        });

        logger.info("Оценки продавца {} успешно получены.", login);
        return ratingModelToEntityMapper.toModelList(ratingDAO.getSellerRatings(seller.getId()));
    }

    @Override
    @Transactional
    public Rating addRating(Rating rating) {
        Optional<UserEntity> optionalSeller = userDAO.findByLogin(rating.getSeller().getLogin());
        UserEntity seller = optionalSeller.orElseThrow(() -> {
            logger.error("Продавца с логином {} не существует.", rating.getSeller().getLogin());
            throw new UserNotFoundException("Продавца с логином " + rating.getSeller().getLogin() + " не существует.");
        });
        Optional<UserEntity> optionalReviewer = userDAO.findByLogin(rating.getReviewer().getLogin());
        UserEntity reviewer = optionalReviewer.orElseThrow(() -> {
            logger.error("Покупателя с логином {} не существует.", rating.getReviewer().getLogin());
            throw new UserNotFoundException("Покупателя с логином " + rating.getReviewer().getLogin() + " не существует.");
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

        logger.info("Оценка продавцу {} успешно добавлена.", rating.getSeller().getLogin());
        return ratingModelToEntityMapper.toModel(ratingEntity);
    }
}