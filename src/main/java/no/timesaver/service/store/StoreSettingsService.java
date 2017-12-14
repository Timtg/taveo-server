package no.timesaver.service.store;

import no.timesaver.dao.StoreSettingsDao;
import no.timesaver.domain.StoreSettings;
import no.timesaver.domain.User;
import no.timesaver.security.AccessValidationService;
import no.timesaver.service.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class StoreSettingsService {

    private final StoreSettingsDao storeSettingsDao;
    private final CurrentUserService currentUserService;
    private final AccessValidationService accessValidationService;

    @Autowired
    public StoreSettingsService(StoreSettingsDao storeSettingsDao, AccessValidationService accessValidationService, CurrentUserService currentUserService) {
        this.storeSettingsDao = storeSettingsDao;
        this.currentUserService = currentUserService;
        this.accessValidationService = accessValidationService;
    }

    public Optional<StoreSettings> getSettingsForStore(Long storeId) {
        User cu = currentUserService.getCurrentUser().orElseThrow(() -> new IllegalStateException("Missing current user for editing a product"));
        if(!accessValidationService.canGetOrModifyOrderForStore(cu, storeId)){
            throw new SecurityException("The current user is not allowed to get settings for the specified store");
        }
        return storeSettingsDao.getSettingsForStore(storeId);
    }

    public Map<Long, StoreSettings> getSettingsForStores(Set<Long> storeIds) {
        return storeSettingsDao.getSettingsForStores(storeIds);
    }

    public void updateSettingsForStore(StoreSettings storeSettings) {
        User cu = currentUserService.getCurrentUser().orElseThrow(() -> new IllegalStateException("Missing current user for editing a product"));
        if(!accessValidationService.userHasAccessToStore(cu, storeSettings.getStoreId())){
            throw new SecurityException("The current user is not allowed to get settings for the specified store");
        }
        storeSettingsDao.updateStoreSettings(storeSettings);
    }

    public List<Long> getStoreIdsForWhichTimeVerificationIsRequired(List<Long> storeIds) {
        return storeSettingsDao.getStoreIdsForWhichTimeVerificationIsRequired(storeIds);
    }
}
