package advertisement.daos.implementations;

import advertisement.daos.interfaces.IAdvertisementDAO;
import advertisement.entities.AdvertisementEntity;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AdvertisementDAO extends GenericDAO<AdvertisementEntity> implements IAdvertisementDAO {
    public AdvertisementDAO() {
        super(AdvertisementEntity.class);
    }

    @Override
    public List<AdvertisementEntity> findWithFilter(String title, String town, List<Long> categoriesIds) {
        String jpql;
        if (categoriesIds.isEmpty()) {
            jpql = "SELECT DISTINCT a FROM AdvertisementEntity a " +
                    "LEFT JOIN a.categories c " +
                    "WHERE (LOWER(a.title) LIKE LOWER(CONCAT(:title, '%')) OR :title IS NULL) " +
                    "AND (LOWER(a.town) LIKE LOWER(CONCAT(:town, '%')) OR :town IS NULL) ";
        }
        else {
            jpql = "SELECT DISTINCT a FROM AdvertisementEntity a " +
                    "LEFT JOIN a.categories c " +
                    "WHERE (LOWER(a.title) LIKE LOWER(CONCAT(:title, '%')) OR :title IS NULL) " +
                    "AND (LOWER(a.town) LIKE LOWER(CONCAT(:town, '%')) OR :town IS NULL) " +
                    "AND (c.id IN :categoryIds)";
        }
        TypedQuery<AdvertisementEntity> query = entityManager.createQuery(jpql, AdvertisementEntity.class);
        query.setParameter("title", title);
        query.setParameter("town", town);
        if (!categoriesIds.isEmpty()) {
            query.setParameter("categoryIds", categoriesIds);
        }

        return query.getResultList();
    }

    @Override
    public Optional<AdvertisementEntity> findByAdNumber(Long id) {
        String jpql = "SELECT a FROM AdvertisementEntity a WHERE a.id = :id";
        TypedQuery<AdvertisementEntity> query = entityManager.createQuery(jpql, AdvertisementEntity.class);
        query.setParameter("id", id);

        return query.getResultList().stream().findFirst();
    }

    @Override
    public List<AdvertisementEntity> findAdvertisementsByUserId(long id) {
        String jpql = "SELECT a FROM AdvertisementEntity a WHERE a.user.id = :id";
        TypedQuery<AdvertisementEntity> query = entityManager.createQuery(jpql, AdvertisementEntity.class);
        query.setParameter("id", id);

        return query.getResultList();
    }
}