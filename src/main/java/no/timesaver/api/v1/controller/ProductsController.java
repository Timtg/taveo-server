package no.timesaver.api.v1.controller;

import no.timesaver.api.v1.controller.linkers.ProductLinker;
import no.timesaver.domain.Product;
import no.timesaver.service.ProductFetchService;
import no.timesaver.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value ="/api/v1/products")
public class ProductsController {

    private final ProductFetchService productFetchService;
    private final ProductLinker productLinker;

    @Autowired
    public ProductsController(ProductFetchService productFetchService, ProductLinker productLinker) {
        this.productFetchService = productFetchService;
        this.productLinker = productLinker;
    }

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    List<Resource<Product>> getActiveProductByIds(@RequestParam List<Long> id){
        return productLinker.listToResource.apply(productFetchService.getActiveProductsByIds(id.stream().collect(Collectors.toSet())));
    }
}
