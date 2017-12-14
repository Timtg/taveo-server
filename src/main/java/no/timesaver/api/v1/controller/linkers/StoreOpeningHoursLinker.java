package no.timesaver.api.v1.controller.linkers;

import no.timesaver.api.v1.controller.StoreController;
import no.timesaver.domain.StoreOpeningHours;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class StoreOpeningHoursLinker {

    public static final Function<StoreOpeningHours,Resource<StoreOpeningHours>> toResource = (s -> {
        Resource<StoreOpeningHours> storeOpeningHoursResource = new Resource<>(s);
        storeOpeningHoursResource.add(linkTo(methodOn(StoreController.class).getStoreOpeningHoursByStoreId(s.getStoreId())).withSelfRel());
        storeOpeningHoursResource.add(linkTo(methodOn(StoreController.class).getStoreById(s.getStoreId())).withRel("store"));
        return storeOpeningHoursResource;
    });

    public static final Function<Optional<StoreOpeningHours>,Resource<StoreOpeningHours>> optionalToResource = (o -> o.map(toResource).orElse(null));

    public static final Function<List<StoreOpeningHours>,List<Resource<StoreOpeningHours>>> listToResource = (list -> list.stream().map(toResource).collect(Collectors.toList()));

}
