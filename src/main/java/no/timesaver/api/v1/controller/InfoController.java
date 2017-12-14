package no.timesaver.api.v1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value ="/api/v1/info")
public class InfoController {


    private final String iconPath;

    @Autowired
    public InfoController(@Value("${timeSaver.iconPath}") String iconPath) {
        this.iconPath = iconPath;
    }

    @RequestMapping(value = "/icon-path", method = RequestMethod.GET)
    public @ResponseBody
    Resource<String> getAllActiveAds(){
        return new Resource<>(iconPath);
    }


}
