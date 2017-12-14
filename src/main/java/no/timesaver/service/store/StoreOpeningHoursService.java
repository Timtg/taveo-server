package no.timesaver.service.store;

import no.timesaver.dao.StoreOpeningHoursDao;
import no.timesaver.domain.StoreOpeningHours;
import no.timesaver.domain.User;
import no.timesaver.security.AccessValidationService;
import no.timesaver.service.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StoreOpeningHoursService {

    private final StoreOpeningHoursDao storeOpeningHoursDao;
    private final CurrentUserService currentUserService;
    private final AccessValidationService accessValidationService;

    @Autowired
    public StoreOpeningHoursService(StoreOpeningHoursDao storeOpeningHoursDao, CurrentUserService currentUserService, AccessValidationService accessValidationService) {
        this.storeOpeningHoursDao = storeOpeningHoursDao;
        this.currentUserService = currentUserService;
        this.accessValidationService = accessValidationService;
    }

    public Optional<StoreOpeningHours> getStoreOpeningHoursByStoreId(Long storeId) {
        return storeOpeningHoursDao.getStoreOpeningHoursByStoreId(storeId);
    }

    public void saveOpeningHoursForStore(StoreOpeningHours openingHours) {
        User currentUser = currentUserService.getCurrentUser().orElseThrow(() -> new IllegalStateException("Missing current user for updating the store"));
        if(!accessValidationService.userHasAccessToStore(currentUser,openingHours.getStoreId())){
            throw new SecurityException("The current user is not allowed to update the opening hours");
        }

        storeOpeningHoursDao.saveOrUpdate(openingHours);
    }
}
