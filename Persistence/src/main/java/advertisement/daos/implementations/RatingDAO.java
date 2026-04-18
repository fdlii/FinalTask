package advertisement.daos.implementations;

import advertisement.daos.interfaces.IRatingDAO;
import advertisement.entities.RatingEntity;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RatingDAO extends GenericDAO<RatingEntity> implements IRatingDAO {
    public RatingDAO() {
        super(RatingEntity.class);
    }

    @Override
    public double getSellerRating(long id) {
        String jpql = "SELECT AVG(r.score) FROM RatingEntity r WHERE r.seller.id = :id";

        Double rating = entityManager
                .createQuery(jpql, Double.class)
                .setParameter("id", id)
                .getSingleResult();
        return rating == null ? 0.0 : rating;
    }

    @Override
    public List<RatingEntity> getSellerRatings(long id) {
        String jpql = "SELECT r FROM RatingEntity r WHERE r.seller.id = :id";

        TypedQuery<RatingEntity> query = entityManager.createQuery(jpql, RatingEntity.class);
        query.setParameter("id", id);
        return query.getResultList();
    }
}
