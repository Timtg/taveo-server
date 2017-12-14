package no.timesaver.service.user;


import no.timesaver.ThreadLocalCurrentUser;
import no.timesaver.dao.UserDao;
import no.timesaver.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CurrentUserService {

    private final UserDao userDao;

    @Autowired
    public CurrentUserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /*
        * returns the logged in user from ThreadLocal - if any, some endpoints are open and do not require auth/jwt
        */
    public Optional<User> getCurrentUser(){
        User user = ThreadLocalCurrentUser.get();
        return Optional.ofNullable(user);
    }

    public User confirmDisclaimer() {
        User user = getCurrentUser().orElseThrow(() -> new IllegalStateException("Unable to verify disclaimer for user without a valid token in the request"));
        user.setAcceptedDisclaimer(userDao.confirmDisclaimerForUserById(user.getId()));
        return user;
    }
}
