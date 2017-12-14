package no.timesaver.api.v1.controller.user;

import no.timesaver.api.v1.dto.AccountDetailsVerificationDto;
import no.timesaver.domain.User;
import no.timesaver.domain.types.UserTypeEnum;
import no.timesaver.exception.InternalServerException;
import no.timesaver.jwt.JwtCreator;
import no.timesaver.service.user.*;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@RestController
@RequestMapping(value ="/api/v1/user")
public class UserCreationController {
    private final static Logger log = LoggerFactory.getLogger(UserCreationController.class);

    private final UserCreatorService userCreatorService;
    private final UserInfoService userInfoService;
    private final JwtCreator jwtCreator;
    private final EmailVerificationService emailVerificationService;
    private final MobileVerificationService mobileVerificationService;
    private final CurrentUserService currentUserService;

    @Autowired
    public UserCreationController(
            UserCreatorService userCreatorService,
            UserInfoService userInfoService,
            JwtCreator jwtCreator,
            EmailVerificationService emailVerificationService,
            MobileVerificationService mobileVerificationService,
            CurrentUserService currentUserService
    ) {
        this.userCreatorService = userCreatorService;
        this.userInfoService = userInfoService;
        this.jwtCreator = jwtCreator;
        this.emailVerificationService = emailVerificationService;
        this.mobileVerificationService = mobileVerificationService;
        this.currentUserService = currentUserService;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody
    Resource<String> createNewUser(@RequestBody User userInfo,@RequestParam String type){
        if(!UserTypeEnum.isValid(type)){
            throw new IllegalArgumentException("Unknown user account type: " + type);
        }
        userInfo.setType(UserTypeEnum.ofDescription(type));

        Optional<Long> id = userCreatorService.create(userInfo);

        if(!id.isPresent()){
            throw new InternalServerException("Unable to create new user");
        }
        User createdUser = userInfoService.getById(id.get());
        try {
            log.info("User {} successfully created, returning jwt",createdUser.getEmail());
            return new Resource<>(jwtCreator.getJwtForUser(createdUser));
        } catch (JoseException |UnsupportedEncodingException e) {
            log.error("Unable to create JWT for newly created user "+createdUser.getEmail()+" due to ",e);
            throw new InternalServerException("Unable to create JWT for user "+createdUser.getEmail()+" due to "+e.getMessage());
        }
    }

    @RequestMapping(value = "/{storeId}", method = RequestMethod.POST)
    public @ResponseBody
    Resource<User> createNewUserForStore(@PathVariable Long storeId, @RequestBody User user){
        if(!UserTypeEnum.isValid(user.getType().toString())){
            throw new IllegalArgumentException("Unknown user account type: " + user.getType());
        }
        user.setStoreId(storeId);
        Optional<Long> id = userCreatorService.createUserForStore(user);
        if(!id.isPresent()){
            throw new InternalServerException("Unable to create new user");
        }
        return new Resource<>(userInfoService.getById(id.get()));
    }

    @RequestMapping(value = "/email/verification", method = RequestMethod.POST)
    public @ResponseBody
    Resource<Boolean> verifyEmail(@RequestBody AccountDetailsVerificationDto dto){
        if(dto == null){
            throw new IllegalArgumentException("Missing email in request");
        }

        User byId = userInfoService.getById(dto.getId());
        if(byId.isEmailVerified()){
            return new Resource<>(true);
        }
        if(!byId.getEmail().equalsIgnoreCase(dto.getVerificationIdentifier())){
            throw new SecurityException("WARNING: Supplied userId does not match the given email-address when verifying email!");
        }
        return new Resource<>(emailVerificationService.verifyEmail(dto.getId(),dto.getOtp()));
    }

    @RequestMapping(value = "/mobile/verification", method = RequestMethod.POST)
    public @ResponseBody
    Resource<String> verifyMobile(@RequestBody AccountDetailsVerificationDto dto) {
        if(dto == null){
            throw new IllegalArgumentException("Missing userName in request");
        }

        User user = userInfoService.getByEmail(dto.getVerificationIdentifier());
        if(!user.isMobileVerified()){
            mobileVerificationService.verifyMobile(user.getId(),dto.getOtp());
            user = userInfoService.getById(user.getId());
        }
        try {
            return new Resource<>(jwtCreator.getJwtForUser(user));
        } catch (JoseException |UnsupportedEncodingException e) {
            log.error("Unable to create JWT after verifying mobile number for user with id "+user.getId()+" due to ",e);
            throw new InternalServerException("Unable to create JWT for user due to "+e.getMessage());
        }
    }

    @RequestMapping(value = "/{userName}/mobile/verification/new", method = RequestMethod.POST)
    public @ResponseBody
    Resource<Boolean> resendVerificationSms(@PathVariable String userName) {
        User user = userInfoService.getByEmail(userName);
        if(user.isMobileVerified()) {
         return null;
        }

        return new Resource<>(mobileVerificationService.generateAndSendSmsVerificationCode(user.getMobile(),user.getId(),user.getName()));
    }

    @RequestMapping(value = "/disclaimer/verification", method = RequestMethod.POST)
    public @ResponseBody
    Resource<String> userConfirmedDisclaimer() {
        User user = currentUserService.confirmDisclaimer();
        try {
            return new Resource<>(jwtCreator.getJwtForUser(user));
        } catch (JoseException |UnsupportedEncodingException e) {
            log.error("Unable to create JWT after verifying mobile number for user with id "+user.getId()+" due to ",e);
            throw new InternalServerException("Unable to create JWT for user due to "+e.getMessage());
        }
    }
}
