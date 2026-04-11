package advertisement.daos.implementations;

import advertisement.daos.interfaces.IAdvertisementDAO;
import advertisement.entities.AdvertisementEntity;
import org.springframework.stereotype.Repository;

@Repository
public class AdvertisementDAO extends GenericDAO<AdvertisementEntity, Long> implements IAdvertisementDAO {
    public AdvertisementDAO() {
        super(AdvertisementEntity.class);
    }
}