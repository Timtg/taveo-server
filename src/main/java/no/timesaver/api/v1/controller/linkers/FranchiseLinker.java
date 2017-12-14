package no.timesaver.api.v1.controller.linkers;

import no.timesaver.api.v1.controller.FranchiseController;
import no.timesaver.domain.Franchise;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class FranchiseLinker {

    private static final Function<Franchise,Resource<Franchise>> toResource = (f -> {
        Resource<Franchise> franchiseResource = new Resource<>(f);
        franchiseResource.add(linkTo(methodOn(FranchiseController.class).getFranchiseById(f.getId())).withSelfRel());
        franchiseResource.add(linkTo(methodOn(FranchiseController.class).getStoresByFranchiseId(f.getId())).withRel("stores"));
        return franchiseResource;
    });

    public static final Function<Optional<Franchise>,Resource<Franchise>> optionalToResource = (f -> f.map(toResource).orElse(null));

    public static final Function<List<Franchise>,List<Resource<Franchise>>> listToResource = (list -> list.stream().map(toResource).collect(Collectors.toList()));

}
