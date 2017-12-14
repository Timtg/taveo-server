package no.timesaver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.timesaver.domain.types.UserTypeEnum;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.util.StringUtils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class User implements UserProperty{

    private Long id;
    private String email;
    private String name;
    private String mobile;
    private Long storeId;

    /*NB NB ! important - do not send pw hash out of the server*/
    @JsonIgnore
    private String passwordHash;
    /*Used when this POJO is used as a DTO during user creation, should otherwise be empty*/
    private String password;

    private UserTypeEnum type;
    private boolean acceptedDisclaimer;
    private boolean emailVerified;
    private boolean mobileVerified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isComplete() {
        return !StringUtils.isEmpty(email) && !StringUtils.isEmpty(name) && !StringUtils.isEmpty(mobile) &&
                isValidEmail() && isValidMobile() && type != null;
    }

    private boolean isValidMobile() {
        String mob = mobile.replace("+","").trim();
        return mob.matches(".*\\d+.*");
    }

    private boolean isValidEmail() {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result && EmailValidator.getInstance().isValid(email);
    }

    public void setType(UserTypeEnum type) {
        this.type = type;
    }

    public UserTypeEnum getType() {
        return type;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    @Override
    @JsonIgnore
    public long getAssociatedUserId() {
        return getId();
    }

    @Override
    @JsonIgnore
    public String getEntityType() {
        return "user";
    }

    @Override
    public boolean moderatorOrPickerHasAccess(User currentUser) {
        return false;
    }

    public void setAcceptedDisclaimer(boolean acceptedDisclaimer) {
        this.acceptedDisclaimer = acceptedDisclaimer;
    }

    public boolean isAcceptedDisclaimer() {
        return acceptedDisclaimer;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setMobileVerified(boolean mobileVerified) {
        this.mobileVerified = mobileVerified;
    }

    public boolean isMobileVerified() {
        return mobileVerified;
    }

    /*NB is null except when used during user creation*/
    public String getPassword() {
        return password;
    }

    /*Should not be used, other than from jackson when used for user creation - set to deprecated due to this*/
    @Deprecated
    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSingleStoreUser() {
        return storeId != null && !storeId.equals(0L) && UserTypeEnum.N.equals(type);
    }
}
