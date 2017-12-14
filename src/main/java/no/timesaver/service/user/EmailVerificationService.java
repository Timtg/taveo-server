package no.timesaver.service.user;

import no.timesaver.dao.EmailVerificationDao;
import no.timesaver.domain.mailTemplate.EmailVerificationModel;
import no.timesaver.service.baseUrl.ServerBasePathService;
import no.timesaver.service.mail.MailCreatorService;
import no.timesaver.service.mail.MailSenderService;
import no.timesaver.service.otp.CodeGenerationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Optional;


@Service
@Transactional
public class EmailVerificationService {
    private final static Logger log = LoggerFactory.getLogger(EmailVerificationService.class);

    private final EmailVerificationDao emailVerificationDao;
    private final PasswordService passwordService;
    private final MailSenderService mailSenderService;
    private final CodeGenerationService codeGenerationService;
    private final ServerBasePathService basePathService;

    @Autowired
    public EmailVerificationService(EmailVerificationDao emailVerificationDao, PasswordService passwordService, MailSenderService mailSenderService, CodeGenerationService codeGenerationService, ServerBasePathService basePathService) {
        this.emailVerificationDao = emailVerificationDao;
        this.passwordService = passwordService;
        this.mailSenderService = mailSenderService;
        this.codeGenerationService = codeGenerationService;
        this.basePathService = basePathService;
    }

    public Boolean verifyEmail(Long userId, String verificationCode) {
        Optional<String> verificationHashForUser = emailVerificationDao.getVerificationHashForUser(userId);
        boolean verified = passwordService.validate(verificationCode, verificationHashForUser.orElseThrow(() -> new IllegalStateException("No email verification stored for user with id " + userId)));
        if(verified){
            emailVerificationDao.setEmailVerifiedForUser(userId);
            emailVerificationDao.removeVerificationEntryByUserId(userId);
            return true;
        }
        return false;
    }

    public boolean generateAndSendEmailVerificationCode(String emailAddress, long userId, String name) {
        String otp = codeGenerationService.generateOTP(6);
        emailVerificationDao.addVerificationEntryForUserId(userId,passwordService.getHash(otp));

        try {
            String callbackUrl = basePathService.getBasePath() + "/confirm-email/?id="+userId+"&otp="+otp+"&email="+ URLEncoder.encode(emailAddress, "UTF-8");
            EmailVerificationModel templateModel = new EmailVerificationModel(name,otp,callbackUrl,emailAddress);
            mailSenderService.sendTemplate(MailCreatorService.FreemarkerMailTemplate.EMAIL_VERIFICATION,emailAddress,templateModel);
            log.info("event=verifyEmailMailSent userName={} userId={}",emailAddress,userId);
            return true;
        } catch (MessagingException|IOException e) {
            log.error("event=verifyEmailSendingFailed cause={}",e.getMessage());
            return false;
        }
    }
}
