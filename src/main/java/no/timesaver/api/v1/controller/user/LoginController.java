package no.timesaver.api.v1.controller.user;

import no.timesaver.ThreadLocalJwt;
import no.timesaver.api.v1.dto.LoginInfo;
import no.timesaver.domain.User;
import no.timesaver.exception.InternalServerException;
import no.timesaver.jwt.JwtCreator;
import no.timesaver.jwt.JwtVerifier;
import no.timesaver.service.user.PasswordService;
import no.timesaver.service.user.UserAccessEnforcer;
import no.timesaver.service.user.UserInfoService;
import no.timesaver.service.user.UserUpdateService;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping(value ="/api/v1/user/auth")
public class LoginController {
    private final static Logger log = LoggerFactory.getLogger(LoginController.class);

    private final UserAccessEnforcer userAccessEnforcer;
    private final JwtCreator jwtCreator;
    private final UserInfoService userInfoService;
    private final PasswordService passwordService;
    private final JwtVerifier jwtVerifier;
    private final UserUpdateService userUpdateService;

    @Autowired
    public LoginController(UserAccessEnforcer userAccessEnforcer, JwtCreator jwtCreator, JwtVerifier jwtVerifier, UserInfoService userInfoService, PasswordService passwordService, UserUpdateService userUpdateService) {
        this.userAccessEnforcer = userAccessEnforcer;
        this.jwtCreator = jwtCreator;
        this.jwtVerifier = jwtVerifier;
        this.userInfoService = userInfoService;
        this.passwordService = passwordService;
        this.userUpdateService = userUpdateService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public @ResponseBody
    Resource<String> loginForJwt(@RequestBody LoginInfo loginInfo){
        if(!loginInfo.isComplete()){
            throw new IllegalArgumentException("Email and password required for login!");
        }
        if(userAccessEnforcer.authenticate(loginInfo.getEmail(),loginInfo.getPassword())){
            User u =userInfoService.getByEmail(loginInfo.getEmail());
            userUpdateService.setLastLogin(u.getId());
            try {
                log.info("User {} successfully authenticated, returning jwt",u.getEmail());
                return new Resource<>(jwtCreator.getJwtForUser(u));
            } catch (JoseException|UnsupportedEncodingException e) {
                log.error("Unable to create JWT for authenticated user "+loginInfo.getEmail()+" due to ",e);
                throw new InternalServerException("Unable to create JWT for authenticated user "+loginInfo.getEmail()+" due to "+e.getMessage());
            }
        } else {
            throw new SecurityException("Unable to authenticate user "+loginInfo.getEmail() +" with the given password!");
        }
    }

    @RequestMapping(value = "/reset/request", method = RequestMethod.POST)
    public @ResponseBody
    Resource<Boolean> requestResetPw(@RequestBody String email){
        if(StringUtils.isEmpty(email)){
            throw new IllegalArgumentException("Email to reset password for cannot be empty");
        }
        User user = userInfoService.getByEmail(email);
        return new Resource<>(passwordService.processResetPwRequest(user));
    }

    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    public @ResponseBody
    Resource<String> resetPw(@RequestBody LoginInfo resetInfo) throws JoseException, UnsupportedEncodingException {
        if(!resetInfo.isComplete() || StringUtils.isEmpty(resetInfo.getResetOtp())){
            throw new IllegalArgumentException("Missing information for resetting password (email, new password or one time passcode");
        }
        User user = userInfoService.getByEmail(resetInfo.getEmail());
        passwordService.resetPw(user,resetInfo.getPassword(),resetInfo.getResetOtp());
        return new Resource<>(jwtCreator.getJwtForUser(user));
    }

    @RequestMapping(value = "/renew", method = RequestMethod.POST)
    public @ResponseBody
    Resource<String> renewExistingToken(@RequestBody String email){
        if(ThreadLocalJwt.get() != null && jwtVerifier.isIssuedByServer(ThreadLocalJwt.get())){
            throw new SecurityException("Missing jwt or jwt not issued by this server");
        }

        if(StringUtils.isEmpty(email)){
            throw new IllegalArgumentException("Email is required for renewal process!");
        }

        if(!userAccessEnforcer.canTokenBeRenewed(email)){
            throw new SecurityException("Supplied email for renewal does not match the contents in the token");
        }

        User u =userInfoService.getByEmail(email);
        try {
            log.info("User {} renewing their token, returning new jwt",u.getEmail());
            return new Resource<>(jwtCreator.getJwtForUser(u));
        } catch (JoseException|UnsupportedEncodingException e) {
            log.error("Unable to renew JWT for user "+email+" due to ",e);
            throw new InternalServerException("Unable to renew JWT for user "+email+" due to "+e.getMessage());
        }
    }
}
