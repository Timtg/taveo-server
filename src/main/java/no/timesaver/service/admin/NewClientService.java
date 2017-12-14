package no.timesaver.service.admin;

import no.timesaver.api.v1.dto.NewInitialClientDto;
import no.timesaver.domain.User;
import no.timesaver.service.FranchiseService;
import no.timesaver.service.store.StoreService;
import no.timesaver.service.user.UserCreatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NewClientService {

    private final FranchiseService franchiseService;
    private final StoreService storeService;
    private final UserCreatorService userCreatorService;

    @Autowired
    public NewClientService(FranchiseService franchiseService, StoreService storeService, UserCreatorService userCreatorService) {
        this.franchiseService = franchiseService;
        this.storeService = storeService;
        this.userCreatorService = userCreatorService;
    }

    public void add(NewInitialClientDto addInfo) {
        Long franchiseId = franchiseService.add(addInfo.getFranchiseName());
        Long storeId = storeService.add(franchiseId,addInfo.getStoreName(),addInfo.getStoreOrgNumber(),addInfo.getStoreLongitude(),addInfo.getStoreLatitude(),
                addInfo.getStoreIconSrc(),addInfo.getStoreContactPhone(),addInfo.getStoreAddress(),addInfo.getStoreContactEmail());

        User userInfo = addInfo.getUserInfoObject();
        userInfo.setStoreId(storeId);
        userCreatorService.createUserForStore(userInfo);

    }
}
