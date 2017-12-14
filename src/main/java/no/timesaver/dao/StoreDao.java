package no.timesaver.dao;

import no.timesaver.domain.Store;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StoreDao extends AbstractDao {

    Optional<Store> getStoreById(Long storeId);

    List<Store> getStoresByFranchiseId(Long franchiseId);

    boolean updateStore(Store update);

    boolean deleteStoreById(Long storeId);

    List<Store> getAllActiveStores();

    Set<String> getStoreNameForIds(List<Long> eligibleStoreIds);

    Long add(Long franchiseId, String storeName, int storeOrgNumber, BigDecimal storeLongitude, BigDecimal storeLatitude, String storeIconSrc, String storeContactPhone, String storeAddress, String storeContactEmail);
}
