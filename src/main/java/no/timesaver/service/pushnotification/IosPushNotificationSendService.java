package no.timesaver.service.pushnotification;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.exceptions.NetworkIOException;
import no.timesaver.exception.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class IosPushNotificationSendService implements PushNotificationSender {
    private final static Logger log = LoggerFactory.getLogger(IosPushNotificationSendService.class);

    private final ApnsService service;

    @Autowired
    public IosPushNotificationSendService(@Value("${apple.certificat.path}") String certificatePath, @Value("${apple.certificat.pw}") String certificatePw) throws IOException {
        InputStream fullCertificatePath = getCertificateAsInputStream(certificatePath);
        this.service = APNS.newService()
                        .withCert(fullCertificatePath, certificatePw )
                        .withSandboxDestination()
                        .build();

    }

    private InputStream getCertificateAsInputStream(String certificatPath) throws IOException {
        return new ClassPathResource(certificatPath).getInputStream();
    }

    @Override
    public boolean push(String deviceToken, String title, String message,Long receiptId) {
        String payload = APNS.newPayload().alertTitle(title).alertBody(message).sound("default").badge(1).build();

        try {
            service.push(deviceToken, payload);
            log.info("event=pushNotificationSent platform=ios to={}",deviceToken);
            return true;
        } catch (NetworkIOException e) {
            log.error("Error occurred when attempting to send Ios push notification to "+deviceToken + ". Cause: "+ e.getMessage());
            return false;
        }
    }
}
