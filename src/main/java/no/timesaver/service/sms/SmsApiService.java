package no.timesaver.service.sms;

import no.timesaver.service.slack.SlackWebHookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.StringJoiner;

@Service
public class SmsApiService {
    private final static Logger log = LoggerFactory.getLogger(SmsApiService.class);

    final static String SENDER = "TIME-SAVER";
    final static String API_URL = "https://api.smsapi.com/sms.do";
    final static String API_URL_BACKUP = "https://api2.smsapi.com/sms.do";


    private final String userName;
    private final String password;
    private final Boolean isTest;
    private final RestTemplate restTemplate;
    private final SlackWebHookService slack;

    @Autowired
    public SmsApiService(@Value("${smsapi.un}") String un, @Value("${smsapi.md5}")String pwAsMd5, @Value("${smsapi.test}")String isTest, RestTemplate restTemplate, SlackWebHookService slack) {
        this.userName = un;
        this.password = pwAsMd5;
        this.isTest = Boolean.valueOf(isTest);
        this.restTemplate = restTemplate;
        this.slack = slack;
    }

    public boolean sendSms(Long to,String message) {
        return sendSms(to.toString(),message);
    }

    public boolean sendSms(String to,String message) {
        log.info("event=sendSms to={}",to);
        ResponseEntity<String> response;

        if(isTest){
            String url = url(to, message, false);
            log.info(url);
        }
        response = restTemplate.getForEntity(url(to,message,false), String.class);
        if(requestFailed(response)){
            response = restTemplate.getForEntity(url(to,message,true), String.class);
        }


        boolean success = !requestFailed(response);
        if(success){
            log.info("event=sentSms to={}",to);
        } else {
            log.info("event=failedSmsSending to={} cause={}",to,getErrorCause(response));
        }
        return success;
    }

    private String getErrorCause(ResponseEntity<String> response) {
        if(!response.getStatusCode().equals(HttpStatus.OK)){
            return response.getStatusCode().getReasonPhrase();
        } else if(!StringUtils.isEmpty(response.getBody()) && response.getBody().contains("ERROR:103")){
            slack.postToServerNotification("Insufficient credits on Your account. <"+ SlackWebHookService.smsApiDashboardUrl +"|Manage account>");
            return "Insufficient credits on Your account";
        }else if(!StringUtils.isEmpty(response.getBody()) && response.getBody().contains("ERROR")){
            return response.getBody();
        }
        return response.getBody() == null ? "Unknown cause" : response.getBody();
    }

    private boolean requestFailed(ResponseEntity<String> response) {
        return !response.getStatusCode().equals(HttpStatus.OK) || StringUtils.isEmpty(response.getBody()) || !response.getBody().contains("OK:");
    }

    private String url(String to,String message,boolean backup) {
        String base = backup ? API_URL_BACKUP : API_URL;

        StringJoiner joiner = new StringJoiner("&",base+"?",testQParam());
        joiner.add("username="+userName);
        joiner.add("password="+password);
        joiner.add("to="+formatTo(to));
        joiner.add("from="+SENDER);
        joiner.add("message="+ message);

        return joiner.toString();
    }

    //TODO: revise when going outside norway
    private String formatTo(String number) {
        String to = number;
        if(!to.startsWith("00") && !to.startsWith("+") ){
            to = "0047"+to;
        }
        return to;
    }

    private String testQParam(){
        return isTest ? "&test=1" : "";
    }


    Optional<String> getCredits() {
        ResponseEntity<String> response = restTemplate.getForEntity(API_URL + "?username="+userName+"&password="+password+"&credits=1", String.class);
        if(response.getStatusCode().is2xxSuccessful()){
            String body = response.getBody();
            return Optional.of(body.substring("Credits: ".length()));
        }
        return Optional.empty();
    }
}
