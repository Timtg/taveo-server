package no.timesaver.service.mail;

import com.sendgrid.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Arrays;

@Service
public class MailSenderService {
    private final static Logger log = LoggerFactory.getLogger(MailSenderService.class);

    private final JavaMailSender mailSender;
    private final MailCreatorService mailCreatorService;
    private final Boolean sendGridIsActive;

    @Autowired
    public MailSenderService(JavaMailSender javaMailSender, MailCreatorService mailCreatorService, @Value("${sendGrid.active}") String sendGridActive) {
        this.mailSender = javaMailSender;
        this.mailCreatorService = mailCreatorService;
        this.sendGridIsActive = Boolean.valueOf(sendGridActive);
    }

    public boolean sendHtmlMail(String toAddress, String title, String contentsAsHtml) throws MessagingException, IOException {
         return send(mailCreatorService.fillHtmlMimeMessage(toAddress,title,contentsAsHtml,mailSender.createMimeMessage()));
    }

    public boolean sendTemplate(MailCreatorService.FreemarkerMailTemplate template, String toAddress, Object templateSubstitutionMap) throws MessagingException, IOException {
        return send(mailCreatorService.getMessageForTemplate(template,toAddress,templateSubstitutionMap,mailSender.createMimeMessage()));
    }

    private boolean send(MimeMessage message) throws IOException, MessagingException {
        if(sendGridIsActive){
            return sendWithSendGrid(message);
        } else {
            mailSender.send(message);
            return true;
        }

    }

    private boolean sendWithSendGrid(MimeMessage message) throws IOException, MessagingException {
        Email from = new Email(Arrays.asList(message.getFrom()).get(0).toString());
        String subject = message.getSubject();
        Email to = new Email(Arrays.asList(message.getRecipients(Message.RecipientType.TO)).get(0).toString());
        Content content = new Content("text/html", message.getContent().toString());
        Mail mail = new Mail(from, subject, to, content);

        String sendgrid_api_key = getSendGridApiToken();
        if(StringUtils.isEmpty(sendgrid_api_key)){
            log.warn("NB! Missing API token for sendGrid!");
            return false;
        }
        
        SendGrid sg = new SendGrid(sendgrid_api_key);
        Request request = new Request();
        try {
            request.method = Method.POST;
            request.endpoint = "mail/send";
            request.body = mail.build();
            Response response = sg.api(request);
            log.info("event=MailSent distributor=sendGrid responseStatusCode:{}",response.statusCode);
            return true;
        } catch (IOException ex) {
            log.error("Error sending mail: {}",message.getSubject(),ex);
            return false;
        }
    }

    private String getSendGridApiToken() {
        String sendgrid_api_key = System.getenv("SENDGRID_API_KEY");
        if(StringUtils.isEmpty(sendgrid_api_key)) {
            sendgrid_api_key = System.getProperty("SENDGRID_API_KEY");
        }
        return sendgrid_api_key;
    }
}
