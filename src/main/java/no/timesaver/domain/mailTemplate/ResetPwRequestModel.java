package no.timesaver.domain.mailTemplate;

import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ResetPwRequestModel extends AbstractTemplateModel {

    private final String name;
    private final String validTo;
    private final String otp;

    public ResetPwRequestModel(String name, LocalDateTime validTo, String otp) {
        if(StringUtils.isEmpty(name) || StringUtils.isEmpty(otp) || validTo == null) {
            throw new IllegalArgumentException("Empty params given when constructing template model");
        }
        this.name = name;
        this.validTo = validTo.format(DateTimeFormatter.ofPattern("dd/MM-yyyy HH:mm:ss"));
        this.otp = otp;
    }

    public String getName() {
        return name;
    }

    public String getValidTo() {
        return validTo;
    }

    public String getOtp() {
        return otp;
    }
}
