package no.timesaver.api.v1.controller;

import no.timesaver.domain.PushNotificationInformation;
import no.timesaver.domain.User;
import no.timesaver.service.pushnotification.PushNotificationInitService;
import no.timesaver.service.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value ="/api/v1/push")
public class PushNotificationController {

    private final PushNotificationInitService pushNotificationInitService;
    private final CurrentUserService currentUserService;

    @Autowired
    public PushNotificationController(PushNotificationInitService pushNotificationInitService, CurrentUserService currentUserService) {
        this.pushNotificationInitService = pushNotificationInitService;
        this.currentUserService = currentUserService;
    }

    @RequestMapping(method = RequestMethod.POST,value = "/",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody
    Boolean register(@RequestBody PushNotificationInformation dto){
        if(!dto.isValid()){
            throw new IllegalArgumentException("Missing required push registration data");
        }

        User currentUser = currentUserService.getCurrentUser().orElseThrow(() -> new IllegalStateException("Missing current user for registring push token"));
        if (!currentUser.getId().equals(dto.getUserId())){
            throw new SecurityException("Access blocked: Attempting to update push token for another user");
        }

        return pushNotificationInitService.register(dto.getUserId(),dto.getToken(),dto.getSystem());
    }
}
