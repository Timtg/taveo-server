package no.timesaver.domain.mailTemplate;

import org.springframework.util.StringUtils;

public class EmailVerificationModel extends AbstractTemplateModel {

    private final String name;
    private final String url;
    private final String otp;
    private final String email;

    public EmailVerificationModel(String name, String otp, String url,String email) {
        if(StringUtils.isEmpty(name) || StringUtils.isEmpty(otp) || StringUtils.isEmpty(url) || StringUtils.isEmpty(email)) {
            throw new IllegalArgumentException("Empty params given when constructing template model");
        }
        this.name = name;
        this.otp = otp;
        this.url = url;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getOtp() {
        return otp;
    }

    public String getUrl() {
        return url;
    }

    public String getEmail() {
        return email;
    }
}
