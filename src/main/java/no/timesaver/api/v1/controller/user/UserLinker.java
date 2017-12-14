package no.timesaver.api.v1.controller.user;

import no.timesaver.api.v1.controller.ReceiptsController;
import no.timesaver.domain.User;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class UserLinker {

    static final Function<User,Resource<User>> toResource = (s -> {
        Resource<User> userResource = new Resource<>(s);
        userResource.add(linkTo(methodOn(UserInfoController.class).getUserById(s.getId())).withSelfRel());
        userResource.add(linkTo(methodOn(ReceiptsController.class).getReceiptsForUser(s.getId(),false)).withRel("receipts"));
        return userResource;
    });

    public static final Function<Optional<User>,Resource<User>> optionalToResource = (o -> o.map(toResource).orElse(null));
}
