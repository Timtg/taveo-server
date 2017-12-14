package no.timesaver.api.v1.dto;

public class AccountDetailsVerificationDto {

    private String verificationIdentifier;
    private Long id;
    private String otp;

    public String getVerificationIdentifier() {
        return verificationIdentifier;
    }

    public void setVerificationIdentifier(String verificationIdentifier) {
        this.verificationIdentifier = verificationIdentifier;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
