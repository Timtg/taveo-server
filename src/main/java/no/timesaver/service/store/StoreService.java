package no.timesaver.service.store;

import no.timesaver.dao.StoreDao;
import no.timesaver.domain.Store;
import no.timesaver.domain.User;
import no.timesaver.exception.DataIntegrityException;
import no.timesaver.security.AccessValidationService;
import no.timesaver.service.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class StoreService {

    private final StoreDao storeDao;
    private final CurrentUserService currentUserService;
    private final AccessValidationService accessValidationService;

    @Autowired
    public StoreService(StoreDao storeDao, CurrentUserService currentUserService, AccessValidationService accessValidationService){
        this.storeDao=storeDao;
        this.currentUserService = currentUserService;
        this.accessValidationService = accessValidationService;
    }

    public Optional<Store> getStoreById(Long storeId) {
        return storeDao.getStoreById(storeId);
    }

    public List<Store> getStoresByFranchiseId(Long franchiseId) {
        return storeDao.getStoresByFranchiseId(franchiseId);
    }

    public Store updateStoreById(Long storeId, Store update) {
        User currentUser = currentUserService.getCurrentUser().orElseThrow(() -> new IllegalStateException("Missing current user for updating the store"));
        if(!accessValidationService.userHasAccessToStore(currentUser,storeId)){
         throw new SecurityException("The current user does is not allowed to update the store in question");
        }

        update.setId(storeId);
        storeDao.updateStore(update);
        Optional<Store> storeById = getStoreById(storeId);
        if(!storeById.isPresent()){
            throw new DataIntegrityException("Unable to find the recently updated store with id "+ storeById);
        }
        return storeById.get();
    }

    public Boolean deleteStoreById(Long storeId) {
        //TODO when(if) this is to be implemented, implement access check
        return storeDao.deleteStoreById(storeId);
    }

    public List<Store> getAllActiveStores() {
        return storeDao.getAllActiveStores();
    }

    public Optional<String> getStoreNameForId(Long storeId) {
        return getStoreNameForIds(Collections.singletonList(storeId)).stream().findFirst();
    }

    public Set<String> getStoreNameForIds(List<Long> storeIds) {
        return storeDao.getStoreNameForIds(storeIds);
    }

    public Long add(Long franchiseId, String storeName, int storeOrgNumber, BigDecimal storeLongitude, BigDecimal storeLatitude, String storeIconSrc, String storeContactPhone, String storeAddress, String storeContactEmail) {
        accessValidationService.adminRequiredValidation();
        return storeDao.add(franchiseId,storeName, storeOrgNumber, storeLongitude, storeLatitude, storeIconSrc, storeContactPhone, storeAddress, storeContactEmail);
    }
}
