package no.timesaver.service.user;

import no.timesaver.dao.MobileVerificationDao;
import no.timesaver.service.otp.CodeGenerationService;
import no.timesaver.service.sms.SmsApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
public class MobileVerificationService {

    private final static Logger log = LoggerFactory.getLogger(EmailVerificationService.class);

    private final MobileVerificationDao mobileVerificationDao;
    private final PasswordService passwordService;
    private final CodeGenerationService codeGenerationService;
    private final SmsApiService smsApiService;

    @Autowired
    public MobileVerificationService(MobileVerificationDao mobileVerificationDao, PasswordService passwordService, CodeGenerationService codeGenerationService, SmsApiService smsApiService) {
        this.mobileVerificationDao = mobileVerificationDao;
        this.passwordService = passwordService;
        this.codeGenerationService = codeGenerationService;
        this.smsApiService = smsApiService;
    }

    public Boolean verifyMobile(Long userId, String verificationCode) {
        Optional<String> verificationHashForUser = mobileVerificationDao.getVerificationHashForUser(userId);
        boolean verified = passwordService.validate(verificationCode, verificationHashForUser.orElseThrow(() -> new IllegalStateException("No mobile verification stored for user with id " + userId)));
        if(verified){
            mobileVerificationDao.setMobileVerifiedForUser(userId);
            mobileVerificationDao.removeVerificationEntryByUserId(userId);
            return true;
        }
        return false;
    }

    public boolean generateAndSendSmsVerificationCode(String mobileNumber, long userId, String name) {
        String otp = codeGenerationService.generateNumericOTP(4);
        mobileVerificationDao.addVerificationEntryForUserId(userId,passwordService.getHash(otp));

        String msg = "Hei " +name+"! Din Taveo-bruker er straks klar. Vennligst fyll inn koden fra denne smsen i appen. Bekreftelseskode: "+otp;
        if(smsApiService.sendSms(mobileNumber, msg)){
            log.info("event=verifyMobileSmsSent mobileNumber={} userId={}",mobileNumber,userId);
            return true;
        } else {
            log.error("event=verifyMobileSendingFailed");
            return false;
        }
    }

}
