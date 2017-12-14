package no.timesaver.api.v1.controller.linkers;

import no.timesaver.api.v1.controller.FranchiseController;
import no.timesaver.api.v1.controller.StoreController;
import no.timesaver.domain.Store;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class StoreLinker {

    public static final Function<Store,Resource<Store>> toResource = (s -> {
        Resource<Store> storeResource = new Resource<>(s);
        storeResource.add(linkTo(methodOn(StoreController.class).getStoreById(s.getId())).withSelfRel());
        storeResource.add(linkTo(methodOn(StoreController.class).getProductsByStoreId(s.getId())).withRel("products"));
        storeResource.add(linkTo(methodOn(StoreController.class).getStoreOpeningHoursByStoreId(s.getId())).withRel("opening-hours"));
        storeResource.add(linkTo(methodOn(FranchiseController.class).getFranchiseById(s.getFranchiseId())).withRel("franchise"));
        return storeResource;
    });

    public static final Function<Optional<Store>,Resource<Store>> optionalToResource = (o -> o.map(toResource).orElse(null));

    public static final Function<List<Store>,List<Resource<Store>>> listToResource = (list -> list.stream().map(toResource).collect(Collectors.toList()));

}
