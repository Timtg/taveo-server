package no.timesaver.service.pushnotification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AndroidPushNotificationSendService implements PushNotificationSender {

    private final static Logger log = LoggerFactory.getLogger(AndroidPushNotificationSendService.class);

    private final RestTemplate restTemplate;
    private final String serverKey;

    @Autowired
    public AndroidPushNotificationSendService(RestTemplate restTemplate, @Value("${google.firebase.SERVER_KEY}") String serverKey) {
        this.restTemplate = restTemplate;
        this.serverKey = serverKey;
    }

    @Override
    public boolean push(String deviceToken, String title, String message,Long receiptId) {
        AndroidPushNotification pushNotification = new AndroidPushNotification(deviceToken, title, message,receiptId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization","key="+serverKey);

        HttpEntity<AndroidPushNotification> notificationHttpEntity = new HttpEntity<>(pushNotification, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("https://fcm.googleapis.com/fcm/send", notificationHttpEntity, String.class);
        if(responseEntity.getStatusCode().is2xxSuccessful()){
            log.info("event=pushNotificationSent platform=android to={}",deviceToken);
            return true;
        } else {
            log.error("Error occurred when attempting to send android push notification to "+deviceToken + ". Cause: "+ responseEntity.getStatusCode().getReasonPhrase());
            return false;
        }


    }

    class AndroidPushNotification {

        private final NotificationData data;
        private final String to;

        AndroidPushNotification(String to, String title, String message,Long receiptId) {
            this.data = new NotificationData(title,message,receiptId);
            this.to = to;
        }

        public NotificationData getData() {
            return data;
        }

        public String getTo() {
            return to;
        }

        class NotificationData {
            private final String title;
            private final String message;
            private final Long receiptId;

            NotificationData(String title, String message, Long receiptId) {
                this.title = title;
                this.message = message;
                this.receiptId = receiptId;
            }

            public String getTitle() {
                return title;
            }

            public String getMessage() {
                return message;
            }

            public Long getReceiptId() {
                return receiptId;
            }
        }

    }
}
