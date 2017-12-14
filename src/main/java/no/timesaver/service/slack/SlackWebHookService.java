package no.timesaver.service.slack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SlackWebHookService {
    public static final String smsApiDashboardUrl = "https://ssl.smsapi.com/#/dashboard/";

    private final String serverNotificationUrl;
    private final RestTemplate restTemplate;

    @Autowired
    public SlackWebHookService(@Value("${slack.webhook.server-notification}") String serverNotificationUrl) {
        this.serverNotificationUrl = serverNotificationUrl;
        this.restTemplate = new RestTemplate();
    }

    public void postToServerNotification(String msg){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<String> stringHttpEntity = new HttpEntity<>("{\"text\": \""+msg+"\"}", headers);
        restTemplate.postForEntity(serverNotificationUrl,stringHttpEntity,String.class);
    }
}
