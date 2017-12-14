package no.timesaver.dao;


import no.timesaver.domain.StoreSettings;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface StoreSettingsDao {
    Optional<StoreSettings> getSettingsForStore(Long storeId);
    Map<Long,StoreSettings> getSettingsForStores(Set<Long> storeIds);
    void updateStoreSettings(StoreSettings storeSettings);

    List<Long> getStoreIdsForWhichTimeVerificationIsRequired(List<Long> storeIds);

    void createStoreSettings(StoreSettings storeSettings);
}
