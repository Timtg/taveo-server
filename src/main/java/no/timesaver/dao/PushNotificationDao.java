package no.timesaver.dao;

import no.timesaver.domain.PushNotificationInformation;

import java.util.List;

public interface PushNotificationDao {
    void registerNewDeviceForUser(Long userId, String token, String system);

    List<PushNotificationInformation> getPushInformationForUserId(Long userId);
}
