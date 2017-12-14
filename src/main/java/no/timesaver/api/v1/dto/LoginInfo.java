package no.timesaver.api.v1.dto;


import org.springframework.util.StringUtils;

public class LoginInfo {

    private String email;
    private String password;
    private String resetOtp;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isComplete() {
        return !StringUtils.isEmpty(email) && !StringUtils.isEmpty(password);
    }

    public String getResetOtp() {
        return resetOtp;
    }

    public void setResetOtp(String resetOtp) {
        this.resetOtp = resetOtp;
    }
}
