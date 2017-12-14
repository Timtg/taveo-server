package no.timesaver.service;

import no.timesaver.dao.FranchiseDao;
import no.timesaver.domain.Franchise;
import no.timesaver.domain.Store;
import no.timesaver.security.AccessValidationService;
import no.timesaver.service.store.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FranchiseService {

    private final StoreService storeService;
    private final FranchiseDao franchiseDao;
    private final AccessValidationService accessValidationService;

    @Autowired
    public FranchiseService(StoreService storeService, FranchiseDao franchiseDao, AccessValidationService accessValidationService) {
        this.storeService = storeService;
        this.franchiseDao = franchiseDao;
        this.accessValidationService = accessValidationService;
    }

    public List<Store> getStoresByFranchiseId(Long franchiseId) {
        return storeService.getStoresByFranchiseId(franchiseId);
    }

    public Optional<Franchise> getById(Long franchiseId) {
        return franchiseDao.getById(franchiseId);
    }

    public Long add(String franchiseName) {
        accessValidationService.adminRequiredValidation();
        return franchiseDao.add(franchiseName);
    }
}
