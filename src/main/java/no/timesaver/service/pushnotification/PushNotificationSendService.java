package no.timesaver.service.pushnotification;

import no.timesaver.dao.PushNotificationDao;
import no.timesaver.domain.PushNotificationInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class PushNotificationSendService {
    private final static Logger log = LoggerFactory.getLogger(PushNotificationSendService.class);

    private final AndroidPushNotificationSendService androidPushNotificationSendService;
    private final IosPushNotificationSendService iosPushNotificationSendService;
    private final PushNotificationDao pushNotificationDao;

    @Autowired
    PushNotificationSendService(AndroidPushNotificationSendService androidPushNotificationSendService, IosPushNotificationSendService iosPushNotificationSendService, PushNotificationDao pushNotificationDao) {
        this.androidPushNotificationSendService = androidPushNotificationSendService;
        this.iosPushNotificationSendService = iosPushNotificationSendService;
        this.pushNotificationDao = pushNotificationDao;
    }

    private Optional<PushNotificationSender> getInstance(String destinationPlatform){
        if(StringUtils.isEmpty(destinationPlatform)){
            return Optional.empty();
        }
        if(destinationPlatform.equals("android")){
            return Optional.of(androidPushNotificationSendService);
        }
        if(destinationPlatform.equals("ios")){
            return Optional.of(iosPushNotificationSendService);
        }
        return Optional.empty();
    }

    public boolean sendNotificationToUser(Long userId,String title,String message, Long receiptId){
        List<PushNotificationInformation> userDeviceInformation = pushNotificationDao.getPushInformationForUserId(userId);
        if(userDeviceInformation.isEmpty()){
            log.warn("No device to push to registered for user with id {}",userId);
        }

        final long[] sendCount = {0};
        userDeviceInformation.forEach(info -> {
            Optional<PushNotificationSender> instance = getInstance(info.getSystem());
            instance.ifPresent(sender -> {
                sender.push(info.getToken(),title,message,receiptId);
                sendCount[0]++;
            });
        });
        return !userDeviceInformation.isEmpty() &&  sendCount[0] > 0;
    }


}
