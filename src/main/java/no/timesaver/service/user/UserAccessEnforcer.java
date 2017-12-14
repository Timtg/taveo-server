package no.timesaver.service.user;

import no.timesaver.dao.UserPasswordDao;
import no.timesaver.domain.User;
import no.timesaver.domain.types.UserTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAccessEnforcer {

    private final PasswordService passwordService;
    private final UserPasswordDao userPasswordDao;
    private final CurrentUserService currentUserService;


    @Autowired
    public UserAccessEnforcer(PasswordService passwordService, UserPasswordDao userPasswordDao,CurrentUserService currentUserService) {
        this.passwordService = passwordService;
        this.userPasswordDao = userPasswordDao;
        this.currentUserService = currentUserService;

    }

    void checkPermissionsForCurrentUserForCreating(User userInfo) {
        /* Admin and moderator creation validation */
        if(UserTypeEnum.A.equals(userInfo.getType()) || UserTypeEnum.M.equals(userInfo.getType())) {
            Optional<User> currentUser = currentUserService.getCurrentUser();
            if(!currentUser.isPresent()){
                throw new SecurityException("No currentUser available, unable to check permissions");
            }
            UserTypeEnum type = currentUser.get().getType();
            if(!UserTypeEnum.A.equals(type)){
                throw new SecurityException("The current user is not an administrator. New admins or moderators can only be created by an admin");
            }
        }

        /* Single store (normal users) or picker creation validation */
        if(userInfo.getStoreId() != null && !userInfo.getStoreId().equals(0L)){
            Optional<User> currentUser = currentUserService.getCurrentUser();
            if(!currentUser.isPresent()){
                throw new SecurityException("No currentUser available, unable to check permissions");
            }
            UserTypeEnum currentUserType = currentUser.get().getType();
            if(!UserTypeEnum.A.equals(currentUserType) && !UserTypeEnum.M.equals(currentUserType)){
                throw new SecurityException("The current user is not an administrator nor a moderator. "+ resolveUserTypeToText(userInfo.getType())+" can only be created by an admin or a moderator");
            }

            if(!currentUser.get().getStoreId().equals(userInfo.getStoreId()) && !UserTypeEnum.A.equals(currentUser.get().getType())){
                throw new SecurityException(resolveUserTypeToText(userInfo.getType())+" can only be created for the same store as the moderator is associated with");
            }
        }

        //User with userTypeEnum == N should be allowed regardless of currentUser, as long as they are not single-store users => public sign-up is enabled
    }

    private String resolveUserTypeToText(UserTypeEnum newUserType){
        if(UserTypeEnum.N.equals(newUserType)) return "Single store users";
        if(UserTypeEnum.M.equals(newUserType)) return "Moderators";
        if(UserTypeEnum.P.equals(newUserType)) return "Pickers";
        return "Unknown user type";
    }

    public boolean authenticate(String email, String password) {
        Optional<String> storedHash =  userPasswordDao.getHashForUserByEmail(email);
        if(!storedHash.isPresent()){
            throw new IllegalArgumentException("Unable to find user with email " + email);
        }
        return passwordService.validate(password, storedHash.get());
    }

    public boolean canTokenBeRenewed(String email) {
        Optional<User> currentUser = currentUserService.getCurrentUser();
        return currentUser.isPresent() && currentUser.get().getEmail().equalsIgnoreCase(email);
    }
}
