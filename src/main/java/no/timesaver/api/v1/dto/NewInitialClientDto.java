package no.timesaver.api.v1.dto;


import no.timesaver.domain.User;
import no.timesaver.domain.types.UserTypeEnum;

import java.math.BigDecimal;

public class NewInitialClientDto {

    private String franchiseName;

    private String storeName;
    private int storeOrgNumber;
    private BigDecimal storeLongitude;
    private BigDecimal storeLatitude;
    private String storeIconSrc;
    private String storeContactPhone;
    private String storeAddress;
    private String storeContactEmail;

    private String moderatorEmail;
    private String moderatorPw;
    private String moderatorName;
    private String moderatorMobile;
    private final UserTypeEnum moderatorUserType = UserTypeEnum.M;
    private final boolean moderatorEmailVerified = true;
    private final boolean moderatorMobileVerified = true;

    public String getFranchiseName() {
        return franchiseName;
    }

    public void setFranchiseName(String franchiseName) {
        this.franchiseName = franchiseName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public int getStoreOrgNumber() {
        return storeOrgNumber;
    }

    public void setStoreOrgNumber(int storeOrgNumber) {
        this.storeOrgNumber = storeOrgNumber;
    }

    public BigDecimal getStoreLongitude() {
        return storeLongitude;
    }

    public void setStoreLongitude(BigDecimal storeLongitude) {
        this.storeLongitude = storeLongitude;
    }

    public BigDecimal getStoreLatitude() {
        return storeLatitude;
    }

    public void setStoreLatitude(BigDecimal storeLatitude) {
        this.storeLatitude = storeLatitude;
    }

    public String getStoreIconSrc() {
        return storeIconSrc;
    }

    public void setStoreIconSrc(String storeIconSrc) {
        this.storeIconSrc = storeIconSrc;
    }

    public String getStoreContactPhone() {
        return storeContactPhone;
    }

    public void setStoreContactPhone(String storeContactPhone) {
        this.storeContactPhone = storeContactPhone;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getStoreContactEmail() {
        return storeContactEmail;
    }

    public void setStoreContactEmail(String storeContactEmail) {
        this.storeContactEmail = storeContactEmail;
    }

    public String getModeratorEmail() {
        return moderatorEmail;
    }

    public void setModeratorEmail(String moderatorEmail) {
        this.moderatorEmail = moderatorEmail;
    }

    public String getModeratorPw() {
        return moderatorPw;
    }

    public void setModeratorPw(String moderatorPw) {
        this.moderatorPw = moderatorPw;
    }

    public String getModeratorName() {
        return moderatorName;
    }

    public void setModeratorName(String moderatorName) {
        this.moderatorName = moderatorName;
    }

    public String getModeratorMobile() {
        return moderatorMobile;
    }

    public void setModeratorMobile(String moderatorMobile) {
        this.moderatorMobile = moderatorMobile;
    }

    public UserTypeEnum getModeratorUserType() {
        return moderatorUserType;
    }

    public boolean isModeratorEmailVerified() {
        return moderatorEmailVerified;
    }

    public boolean isModeratorMobileVerified() {
        return moderatorMobileVerified;
    }

    public User getUserInfoObject() {
        User u = new User();
        u.setEmail(moderatorEmail);
        //Ignore deprecation
        u.setPassword(moderatorPw);
        u.setName(moderatorName);
        u.setMobile(moderatorMobile);
        u.setType(moderatorUserType);
        u.setEmailVerified(moderatorEmailVerified);
        u.setMobileVerified(moderatorMobileVerified);

        return u;
    }
}
