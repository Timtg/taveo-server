package no.timesaver.service.pushnotification;

public interface PushNotificationSender {

    boolean push(String deviceToken,String title,String message, Long receiptId);
}
