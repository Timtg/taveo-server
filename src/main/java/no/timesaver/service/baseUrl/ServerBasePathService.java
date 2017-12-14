package no.timesaver.service.baseUrl;

import org.springframework.stereotype.Service;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Service
public class ServerBasePathService {

    public String getBasePath() {
        return extractBaseUrl(linkTo(methodOn(ServeBaseUrlResolver.class).dummy()).toUri().toString());
    }

    private String extractBaseUrl(String fullUrl){
        return fullUrl.substring(0,fullUrl.indexOf("/foo-bar-baz"));
    }
}
