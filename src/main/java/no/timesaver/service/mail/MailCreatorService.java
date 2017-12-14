package no.timesaver.service.mail;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import no.timesaver.exception.MailTemplateGenerationException;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailCreatorService {

    private final static String NO_REPLY = "no-reply@time-saver.no";
    private final static String templateDir = "/fmtemplates/";

    private final  Configuration freemarkerConfiguration;


    public enum FreemarkerMailTemplate {
        PW_RESET_REQUEST("resetPwRequestTemplate.html","Taveo: Password reset requested"),
        PW_RESET_CONFIRMATION("resetPwConfirmationTemplate.html","Taveo: Password reset performed"),
        EMAIL_VERIFICATION("verifyEmailTemplate.html","Taveo: Account created, verify email");


        private final String templateFileName;
        private final String mailSubject;
        FreemarkerMailTemplate(String templateFileName, String mailSubject){
            this.templateFileName =templateFileName;
            this.mailSubject = mailSubject;
        }
        public String getTemplateFileName(){
            return templateFileName;
        }

        public String getMailSubject() {
            return mailSubject;
        }
    }


    public MailCreatorService() throws IOException, TemplateException {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setTemplateLoaderPath(templateDir);
        configurer.afterPropertiesSet();
        this.freemarkerConfiguration = configurer.getConfiguration();
    }

    MimeMessage fillHtmlMimeMessage(String toAddress, String title, String contentsAsHtml, MimeMessage mimeMessage) throws MessagingException {
        if(!validateMailAddress(toAddress)){
            throw new IllegalArgumentException("The to-address " + toAddress + " is not a valid email address");
        }
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setFrom(NO_REPLY);
        helper.setTo(toAddress);
        helper.setSubject(title);
        helper.setText(contentsAsHtml,true);
        return mimeMessage;
    }

    MimeMessage getMessageForTemplate(FreemarkerMailTemplate template, String toAddress, Object templateModel, MimeMessage mimeMessage) throws MessagingException {
        String content = getContentForTemplate(template, toAddress, templateModel);
        return fillHtmlMimeMessage(toAddress,template.getMailSubject(),content,mimeMessage);
    }

    private String getContentForTemplate(FreemarkerMailTemplate template, String toAddress, Object templateModel) {
        if(!validateMailAddress(toAddress)){
            throw new IllegalArgumentException("The to-address " + toAddress + " is not a valid email address");
        }
        String content;
        try{
            Map<String, Object> model = new HashMap<>();
            model.put("root",templateModel);
            Template freemakerTemplate = freemarkerConfiguration.getTemplate(template.getTemplateFileName());
            content = FreeMarkerTemplateUtils.processTemplateIntoString(freemakerTemplate, model);
        }catch(Exception e){
            throw new MailTemplateGenerationException("Error occurred when generating mail content from template ",e);
        }
        return content;
    }

    private boolean validateMailAddress(String addr){
        try {
            new InternetAddress(addr);
        } catch (AddressException e) {
            return false;
        }
        return true;
    }
}
