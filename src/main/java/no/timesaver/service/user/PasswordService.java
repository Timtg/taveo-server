package no.timesaver.service.user;

import no.timesaver.dao.UserPasswordDao;
import no.timesaver.domain.User;
import no.timesaver.domain.mailTemplate.ResetPwConfirmationModel;
import no.timesaver.domain.mailTemplate.ResetPwRequestModel;
import no.timesaver.service.mail.MailCreatorService;
import no.timesaver.service.mail.MailSenderService;
import no.timesaver.service.otp.CodeGenerationService;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PasswordService {
    private final static Logger log = LoggerFactory.getLogger(PasswordService.class);
    private final UserPasswordDao userPasswordDao;
    private final CodeGenerationService codeGenerationService;
    private final MailSenderService mailSenderService;

    @Autowired
    public PasswordService(UserPasswordDao userPasswordDao, CodeGenerationService codeGenerationService, MailSenderService mailSenderService) {
        this.userPasswordDao = userPasswordDao;
        this.codeGenerationService = codeGenerationService;
        this.mailSenderService = mailSenderService;
    }

    private final int SALT_ROUNDS = 12;
    private final int RESET_CODE_VALID_MIN = 30;

    String getHash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(SALT_ROUNDS));
    }

    public boolean validate(String password, String hash){
        StopWatch watch = new StopWatch();
        watch.start();
        boolean validPw = BCrypt.checkpw(password, hash);
        watch.stop();
        log.info("Check hash took {}ms",watch.getTotalTimeMillis());
        return validPw;
    }

    public Boolean processResetPwRequest(User user) {
        String otp = codeGenerationService.generateOTP(8);
        String hash = getHash(otp);
        LocalDateTime resetValidTo = getResetValidToDateTime();

        userPasswordDao.setResetInfo(user.getId(),hash,resetValidTo);
        log.info("otp={}",otp);

        ResetPwRequestModel templateModel = new ResetPwRequestModel(user.getName(),resetValidTo,otp);
        try {
            return mailSenderService.sendTemplate(MailCreatorService.FreemarkerMailTemplate.PW_RESET_REQUEST,user.getEmail(),templateModel);
        } catch (MessagingException|IOException e) {
          log.error("Error sending reset request mail: ",e);
          return false;
        }
    }

    private LocalDateTime getResetValidToDateTime() {
        return LocalDateTime.now().plusMinutes(RESET_CODE_VALID_MIN);
    }

    public Boolean resetPw(User user, String password, String resetOtp) {
        Optional<String> resetCodeHash = userPasswordDao.getResetCodeHashIfValid(user.getId());
        if(!resetCodeHash.isPresent()){
            throw new IllegalStateException("No resetRequest for the specified user has been made, or the validity of the one time code has expired");
        }
        if(!validate(resetOtp, resetCodeHash.get())){
            throw new SecurityException("The supplied one time code does not match the one stored for the sent reset request for the specified user");
        }
        userPasswordDao.resetPassword(user.getId(),getHash(password));

        ResetPwConfirmationModel templateModel = new ResetPwConfirmationModel(user.getName());
        try{
            mailSenderService.sendTemplate(MailCreatorService.FreemarkerMailTemplate.PW_RESET_CONFIRMATION,user.getEmail(),templateModel);
            return true;
        } catch (MessagingException|IOException e) {
            log.error("Error sending reset confirmation mail: ",e);
            return false;
        }
    }
}
