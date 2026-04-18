package advertisement.services.implementations;

import advertisement.daos.interfaces.IRatingDAO;
import advertisement.daos.interfaces.IUserDAO;
import advertisement.entities.RatingEntity;
import advertisement.entities.UserEntity;
import advertisement.mappers.IRatingModelToEntityMapper;
import advertisement.models.Rating;
import advertisement.services.interfaces.IRatingService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class RatingService implements IRatingService {
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
        UserEntity seller = optionalSeller.orElseThrow();

        return ratingModelToEntityMapper.toModelList(ratingDAO.getSellerRatings(seller.getId()));
    }

    @Override
    @Transactional
    public Rating addRating(Rating rating) {
        Optional<UserEntity> optionalSeller = userDAO.findByLogin(rating.getSeller().getLogin());
        UserEntity seller = optionalSeller.orElseThrow();
        Optional<UserEntity> optionalReviewer = userDAO.findByLogin(rating.getReviewer().getLogin());
        UserEntity reviewer = optionalReviewer.orElseThrow();
        if (seller.getId() == reviewer.getId()){
            throw new IllegalArgumentException("Нельзя поставить оценку самому себе!");
        }

        Instant instant = Instant.now();
        RatingEntity ratingEntity = ratingModelToEntityMapper.toEntity(rating);
        ratingEntity.setWrittenAt(instant);
        ratingEntity.setSeller(seller);
        ratingEntity.setReviewer(reviewer);

        ratingDAO.save(ratingEntity);

        double score = ratingDAO.getSellerRating(seller.getId());
        ratingEntity.getSeller().setSellerRating(score);

        return ratingModelToEntityMapper.toModel(ratingEntity);
    }
}
