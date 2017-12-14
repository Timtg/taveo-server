package no.timesaver.service.sms;

import no.timesaver.service.slack.SlackWebHookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CreditCheckScheduledTask {

    private final static Logger log = LoggerFactory.getLogger(CreditCheckScheduledTask.class);
    private final SlackWebHookService slack;
    private final SmsApiService smsApiService;

    @Autowired
    public CreditCheckScheduledTask(SlackWebHookService slackWebHookService, SmsApiService smsApiService) {
        this.slack = slackWebHookService;
        this.smsApiService = smsApiService;
    }

    @Scheduled(fixedDelay = 1000L * 60 *30) //each 30min
    public void checkSmsApiCredits() {
        smsApiService.getCredits().ifPresent(credits -> {
            double creditsParsed = Double.parseDouble(credits);
            if(creditsParsed < 10){
                slack.postToServerNotification("Low credits on your SMS-API account: "+creditsParsed+" Action needed! <"+ SlackWebHookService.smsApiDashboardUrl +"|Manage account>");
                log.warn("WARNING! Low credits on SMS-API account: "+creditsParsed+"! Action needed!");
            }
        });
    }
}
