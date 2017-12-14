package no.timesaver.domain;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

public class PushNotificationInformation {

    private Long userId;
    private String token;
    private String system;

    private final List<String> validSystems = Arrays.asList("android","ios");

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        if(!StringUtils.isEmpty(system)) {
            this.system = system.toLowerCase();
        }
    }

    public boolean isValid(){
        return userId != null && !userId.equals(0L) && !StringUtils.isEmpty(token) && !StringUtils.isEmpty(system) && validSystems.contains(system);
    }
}
