package advertisement.daos.interfaces;

import advertisement.entities.RatingEntity;

import java.util.List;

public interface IRatingDAO extends IGenericDAO<RatingEntity> {
    double getSellerRating(long id);
    List<RatingEntity> getSellerRatings(long id);
}
