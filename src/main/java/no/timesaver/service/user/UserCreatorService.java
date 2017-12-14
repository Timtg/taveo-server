package no.timesaver.service.user;

import no.timesaver.dao.UserCreatorDao;
import no.timesaver.dao.UserDao;
import no.timesaver.domain.User;
import no.timesaver.exception.WeakPasswordException;
import no.timesaver.tools.StringChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserCreatorService {
    private final static Logger log = LoggerFactory.getLogger(UserCreatorService.class);

    private final UserCreatorDao userCreatorDao;
    private final UserDao userDao;
    private final PasswordService passwordService;
    private final UserAccessEnforcer userAccessEnforcer;
    private final EmailVerificationService emailVerificationService;
    private final MobileVerificationService mobileVerificationService;

    @Autowired
    public UserCreatorService(
            UserCreatorDao userCreatorDao,
            UserDao userDao,
            PasswordService passwordService,
            UserAccessEnforcer userAccessEnforcer,
            EmailVerificationService emailVerificationService,
            MobileVerificationService mobileVerificationService
    ) {
        this.userCreatorDao = userCreatorDao;
        this.userDao = userDao;
        this.passwordService = passwordService;
        this.userAccessEnforcer = userAccessEnforcer;
        this.emailVerificationService = emailVerificationService;
        this.mobileVerificationService = mobileVerificationService;
    }

    /**
     *
     * @param userInfo to create the new user from
     * @return id of the newly created user
     */
    public Optional<Long> create(User userInfo) {
        String password = userInfo.getPassword();
        validateUserInfo(userInfo, password);

        userInfo.setPasswordHash(passwordService.getHash(password));
        Number generatedId = userCreatorDao.createNew(userInfo);
        if(generatedId == null) {
            return Optional.empty();
        }
        Long id = generatedId.longValue();
        log.info("event=userCreated userName={} userId={}",userInfo.getEmail(),id);

        emailVerificationService.generateAndSendEmailVerificationCode(userInfo.getEmail(),id,userInfo.getName());
        mobileVerificationService.generateAndSendSmsVerificationCode(userInfo.getMobile(),id,userInfo.getName());

        return Optional.of(id);
    }


    public Optional<Long> createUserForStore(User userInfo) {
        String password = userInfo.getPassword();

        validateUserInfo(userInfo, password);
        //If user to be created is a singleStoreUser and the validation has passed (current user can create singleStoreUsers) certain fields should be set on the new user
        if(userInfo.isSingleStoreUser()){
            userInfo.setMobileVerified(true);
            userInfo.setEmailVerified(true);
            userInfo.setAcceptedDisclaimer(true);
        }

        userInfo.setPasswordHash(passwordService.getHash(password));

        Number generatedId = userCreatorDao.createNew(userInfo);
        if(generatedId == null) {
            return Optional.empty();
        }
        Long id = generatedId.longValue();
        log.info("event=userCreated userName={} userId={}",userInfo.getEmail(),id);
        return Optional.of(id);
    }

    private void validateUserInfo(User userInfo, String password) {
        if(!userInfo.isComplete()){
            throw new IllegalArgumentException("user information incomplete for creating new user");
        }
        userAccessEnforcer.checkPermissionsForCurrentUserForCreating(userInfo);

        boolean isNewUserInfo = userCreatorDao.canBeCreated(userInfo.getEmail(),userInfo.getMobile());
        if(!isNewUserInfo){
            throw new IllegalArgumentException("The email or the phone number has already been registered");
        }
        validatePasswordStrength(password);
    }




    /**
     * Throws WeakPasswordxception
     * @param password
     */
    private boolean validatePasswordStrength(String password) {
        if(password == null || password.length() < 6){
            throw new WeakPasswordException("Password too short");
        }
        if(!password.matches(".*\\d+.*")){
            throw new WeakPasswordException("The password must contain at least one digit");
        }
        if(!StringChecker.hasUpperCaseLetters(password)){
            throw new WeakPasswordException("The password must contain at least one upper case letter");
        }
        if(!StringChecker.hasLowerCaseLetters(password)){
            throw new WeakPasswordException("The password must contain at least one lower case letter");
        }
        return true;
    }
}
