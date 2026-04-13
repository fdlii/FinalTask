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