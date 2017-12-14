package no.timesaver.dao;

import no.timesaver.domain.StoreOpeningHours;

import java.util.Optional;

public interface StoreOpeningHoursDao extends AbstractDao{


    Optional<StoreOpeningHours> getStoreOpeningHoursByStoreId(Long storeId);

    void saveOrUpdate(StoreOpeningHours openingHours);
}
