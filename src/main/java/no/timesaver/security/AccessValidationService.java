package no.timesaver.security;

import no.timesaver.domain.User;
import no.timesaver.domain.UserProperty;
import no.timesaver.domain.types.UserTypeEnum;
import no.timesaver.service.user.CurrentUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class AccessValidationService {
    private final static Logger log = LoggerFactory.getLogger(AccessValidationService.class);

    private final CurrentUserService currentUserService;

    @Autowired
    public AccessValidationService(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    public boolean adminRequiredValidation() {
        if (currentUserService.getCurrentUser().orElseThrow(() -> new SecurityException("Missing current user for creating new client")).getType() != UserTypeEnum.A) {
            throw new SecurityException("Current user is not an admin. API restricted!");
        }
        return true;
    }

    public boolean validateAccess(UserProperty validationEntity) {
        Optional<User> optionalCurrentUser = currentUserService.getCurrentUser();
        if(optionalCurrentUser.isPresent()) {
            User currentUser = optionalCurrentUser.get();
            if (!UserTypeEnum.A.equals(currentUser.getType())) {
                if (!currentUser.getId().equals(validationEntity.getAssociatedUserId()) && !validationEntity.moderatorOrPickerHasAccess(currentUser)) {
                    log.warn("Access restricted: Current user {}({}) of type {} attempted to get {} with id {}",currentUser.getName(),currentUser.getEmail(),currentUser.getType().name(),validationEntity.getEntityType(),validationEntity.getAssociatedUserId());
                    throw new SecurityException("Restricted access: User "+currentUser.getId() + " not allowed to get " + validationEntity.getEntityType() + " for another user ("+validationEntity.getAssociatedUserId()+")");
                }
            }
        }
        return true;
    }

    public boolean canCurrentUserGetUserNameForReceipt(List<Long> storeIdsFromReceipt){
        Optional<User> optionalCurrentUser = currentUserService.getCurrentUser();
        if(optionalCurrentUser.isPresent()) {
            if(!UserTypeEnum.A.equals(optionalCurrentUser.get().getType()) && !storeIdsFromReceipt.contains(optionalCurrentUser.get().getStoreId())){
                throw new SecurityException("Restricted access: Attempted to fetch information for the user of a order, for which the logged in user is not associated with any of the stores in the order");
            }
        }
        return true;
    }

    public boolean userHasAccessToStore(User currentUser, Long storeId){


        if(UserTypeEnum.A.equals(currentUser.getType())){
            return true;
        }

        if(!UserTypeEnum.M.equals(currentUser.getType())){
            return false;
        }

        return storeId.equals(currentUser.getStoreId());
    }

    public boolean canEditOtherUser(User currentUser, Long storeIdForUserToBeEdited) {
        return userHasAccessToStore(currentUser,storeIdForUserToBeEdited);
    }

    public boolean canGetUsersForStore(User currentUser, Long storeId) {
        return userHasAccessToStore(currentUser,storeId);
    }

    public boolean canEditProductForStore(User currentUser, Long storeIdForProduct) {
        return userHasAccessToStore(currentUser,storeIdForProduct);
    }

    public boolean canGetOrModifyOrderForStore(User currentUser, Long storeId) {
        return currentUser.getStoreId().equals(storeId) &&
                Arrays.asList(UserTypeEnum.A, UserTypeEnum.M, UserTypeEnum.P).contains(currentUser.getType());

    }
}
