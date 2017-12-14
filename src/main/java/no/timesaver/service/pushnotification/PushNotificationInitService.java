package no.timesaver.service.pushnotification;

import no.timesaver.dao.PushNotificationDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationInitService {

    private final static Logger log = LoggerFactory.getLogger(PushNotificationInitService.class);
    private final PushNotificationDao pushNotificationDao;

    @Autowired
    public PushNotificationInitService(PushNotificationDao pushNotificationDao) {
        this.pushNotificationDao = pushNotificationDao;
    }

    public Boolean register(Long userId, String token, String system) {
        pushNotificationDao.registerNewDeviceForUser(userId,token,system);

        log.info("event=pushTokenRegistered token={} userId={} system={}", token,userId,system);
        return true;
    }
}
