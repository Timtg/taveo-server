package no.timesaver.service.user;

import no.timesaver.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserUpdateService {

    private final UserDao userDao;

    @Autowired
    public UserUpdateService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setLastLogin(Long userId) {
        setLastLogin(userId,LocalDateTime.now());
    }

    public void setLastLogin(Long userId,LocalDateTime time) {
        userDao.setLastLogin(userId,time);
    }
}
