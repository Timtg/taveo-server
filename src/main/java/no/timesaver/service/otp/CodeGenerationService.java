package no.timesaver.service.otp;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class CodeGenerationService {
    private static final int LENGTH = 6;
    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUM = "0123456789";
    private static SecureRandom rnd = new SecureRandom();

    public String generateConfirmationCode() {
        return generateCodeOfLength(LENGTH,true);
    }

    public String generateOTP(int len) {
        return generateCodeOfLength(len,true);
    }

    public String generateNumericOTP(int len) {
        return generateCodeOfLength(len,false);
    }

    private String generateCodeOfLength(int length,boolean mixedMode) {
        String src = mixedMode ? AB : NUM;
        StringBuilder sb = new StringBuilder(length);
        for( int i = 0; i < length; i++ )
            sb.append( src.charAt( rnd.nextInt(src.length()) ) );
        return sb.toString();
    }
}
