package no.timesaver.service.user;

import no.timesaver.dao.UserDao;
import no.timesaver.domain.Receipt;
import no.timesaver.domain.User;
import no.timesaver.domain.types.UserTypeEnum;
import no.timesaver.security.AccessValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserInfoService {

    private final UserDao userDao;
    private final AccessValidationService accessValidationService;
    private final CurrentUserService currentUserService;

    @Autowired
    public UserInfoService(UserDao userDao, AccessValidationService accessValidationService,CurrentUserService currentUserService) {
        this.userDao = userDao;
        this.accessValidationService = accessValidationService;
        this.currentUserService = currentUserService;
    }

    public User getById(Long userId) {
        User user = getUserIfExists(userId);
        accessValidationService.validateAccess(user);
        return user;
    }

    public Optional<String> getUserNameForReceipt(Receipt receipt){
        accessValidationService.canCurrentUserGetUserNameForReceipt(receipt.allStoreIds());
        return userDao.getUserNameById(receipt.getUserId());
    }

    public User getByEmail(String email) {
        Optional<User> user = userDao.findByEmail(email.trim().replace("\n",""));
        if(!user.isPresent()){
            throw new IllegalArgumentException("Unable to find user with email " + email);
        }
        accessValidationService.validateAccess(user.get());
        return user.get();
    }

    public Optional<Long> getStoreIdForUserId(Long userId) {
        return userDao.getStoreIdForUser(userId);
    }



    public Optional<List<User>> getUsersForStore(Long storeId) {
        User currentUser = currentUserService.getCurrentUser().orElseThrow(() -> new IllegalStateException("No current user found, fetching of users for store is restricted"));
        if(accessValidationService.canGetUsersForStore(currentUser,storeId)) {
            return userDao.getUsersForStore(storeId);
        }
        return Optional.empty();
    }

    public boolean deleteUserById(Long userId) {
        if(!userExists(userId)){
            throw new IllegalArgumentException("Unable to find user with id " + userId);
        }
        User currentUser = currentUserService.getCurrentUser().orElseThrow(() -> new IllegalStateException("No current user found, fetching of users for store is restricted"));

        return getStoreIdForUserId(userId)
                .map(storeIdForUserToBeEdited -> {
                            if(accessValidationService.canEditOtherUser(currentUser, storeIdForUserToBeEdited)){
                                return userDao.deleteUserById(userId);
                            }
                            return false;
                        }
                )
                .orElseThrow(() -> new IllegalArgumentException("Not allowed to delete users not associated with a specific store"));
    }


    private boolean userExists(Long userId) {
        return userDao.userExists(userId);
    }

    private User getUserIfExists(Long userId) {
        if(!userExists(userId)){
            throw new IllegalArgumentException("Unable to find user with id " + userId);
        }
        //Optional implicit checked by previous dao call
        return userDao.findById(userId).get(); //NOSONAR
    }

    public boolean editUser(User user) {
        User currentUser = currentUserService.getCurrentUser().orElseThrow(() -> new IllegalStateException("No current user found, update of other users is restricted"));
        return getStoreIdForUserId(user.getId())
                .map(storeIdForUserToBeEdited -> {
                            if(accessValidationService.canEditOtherUser(currentUser, storeIdForUserToBeEdited)){
                                return userDao.updateStoreUser(user);
                            }
                            return false;
                        }
                )
                .orElseThrow(() -> new IllegalArgumentException("Not allowed to edit normal users not associated with a specific store"));
    }
}
