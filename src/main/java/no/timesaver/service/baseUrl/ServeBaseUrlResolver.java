package no.timesaver.service.baseUrl;

import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/foo-bar-baz")
public class ServeBaseUrlResolver {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody
    Resource<Boolean> dummy(){return new Resource<>(true);}
}
