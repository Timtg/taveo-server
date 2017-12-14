package no.timesaver.api.v1.controller.linkers;

import no.timesaver.api.v1.controller.ProductController;
import no.timesaver.api.v1.controller.ProductPriceController;
import no.timesaver.api.v1.controller.StoreController;
import no.timesaver.domain.Product;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ProductLinker {
    private static final Function<Product,Resource<Product>> toResource = (p -> {
        Resource<Product> ProductResource = new Resource<>(p);
        ProductResource.add(linkTo(methodOn(ProductController.class).getProductById(p.getId())).withSelfRel());
        ProductResource.add(linkTo(methodOn(StoreController.class).getStoreById(p.getStoreId())).withRel("store"));
        ProductResource.add(linkTo(methodOn(ProductPriceController.class).getPriceForProductById(p.getStoreId())).withRel("price"));
        return ProductResource;
    });

    public static final Function<Optional<Product>,Resource<Product>> optionalToResource = (f -> f.map(toResource).orElse(null));

    public static final Function<List<Product>,List<Resource<Product>>> listToResource = (list -> list.stream().map(toResource).collect(Collectors.toList()));

}
