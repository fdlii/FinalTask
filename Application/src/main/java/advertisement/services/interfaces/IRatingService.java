package advertisement.services.interfaces;

import advertisement.models.Rating;

import java.util.List;

public interface IRatingService {
    Rating addRating(Rating rating);
    List<Rating> getSellerRatings(String login);
}
