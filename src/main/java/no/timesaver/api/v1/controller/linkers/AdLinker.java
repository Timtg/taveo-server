package no.timesaver.api.v1.controller.linkers;

import no.timesaver.api.v1.controller.AdsController;
import no.timesaver.api.v1.controller.ProductController;
import no.timesaver.api.v1.controller.StoreController;
import no.timesaver.domain.Advertisement;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class AdLinker {

    private static final Function<Advertisement,Resource<Advertisement>> toResource = (s -> {
        Resource<Advertisement> adResource = new Resource<>(s);
        adResource.add(linkTo(methodOn(AdsController.class).getAdById(s.getId())).withSelfRel());
        adResource.add(linkTo(methodOn(ProductController.class).getProductById(s.getProductId())).withRel("product"));
        adResource.add(linkTo(methodOn(StoreController.class).getStoreById(s.getStoreId())).withRel("store"));
        return adResource;
    });

    public static final Function<Optional<Advertisement>,Resource<Advertisement>> optionalToResource = (o -> o.map(toResource).orElse(null));

    public static final Function<List<Advertisement>,List<Resource<Advertisement>>> listToResource = (list -> list.stream().map(toResource).collect(Collectors.toList()));

}
