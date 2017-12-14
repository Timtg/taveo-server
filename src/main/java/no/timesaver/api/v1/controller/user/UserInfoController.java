package no.timesaver.api.v1.controller.user;

import no.timesaver.domain.User;
import no.timesaver.service.user.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value ="/api/v1/user/info")
public class UserInfoController {

    private final UserInfoService userInfoService;
    private final UserLinker userLinker;

    @Autowired
    public UserInfoController(UserInfoService userInfoService,UserLinker userLinker) {
        this.userInfoService = userInfoService;
        this.userLinker = userLinker;
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public @ResponseBody
    Resource<User> getUserById(@PathVariable Long userId){
        User userById = userInfoService.getById(userId);
        return userLinker.toResource.apply(userById);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public @ResponseBody
    boolean editUser(@RequestBody User user){
    return userInfoService.editUser(user);
    }

    @RequestMapping(value = "/store/{userId}", method = RequestMethod.GET)
    public @ResponseBody
    Optional<Long> getStoreIdForUser(@PathVariable Long userId){
        return userInfoService.getStoreIdForUserId(userId);
    }

    @RequestMapping(value = "/users/{storeId}", method = RequestMethod.GET)
    public @ResponseBody
    Optional<List<User>> getUsersForStore(@PathVariable Long storeId){
        return userInfoService.getUsersForStore(storeId);
    }
    @RequestMapping(value = "/delete/{userId}", method = RequestMethod.PUT)
    public @ResponseBody
    boolean deleteUserById(@PathVariable Long userId){
        return userInfoService.deleteUserById(userId);
    }

}
