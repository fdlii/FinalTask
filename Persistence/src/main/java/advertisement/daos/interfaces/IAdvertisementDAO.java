package advertisement.daos.interfaces;

import advertisement.entities.AdvertisementEntity;

import java.util.List;
import java.util.Optional;

public interface IAdvertisementDAO extends IGenericDAO<AdvertisementEntity> {
    Optional<AdvertisementEntity> findByAdNumber(Long id);
    List<AdvertisementEntity> findAdvertisementsByUserId(long id);
    List<AdvertisementEntity> findWithFilter(String title, String town, List<Long> categoriesIds);
}
