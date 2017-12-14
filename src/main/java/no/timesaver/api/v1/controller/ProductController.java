package no.timesaver.api.v1.controller;

import no.timesaver.api.v1.controller.linkers.ProductLinker;
import no.timesaver.domain.Product;
import no.timesaver.service.ProductFetchService;
import no.timesaver.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value ="/api/v1/product")
public class ProductController {

    private final ProductFetchService productFetchService;
    private final ProductService productService;
    private final ProductLinker productLinker;

    @Autowired
    public ProductController(ProductService productService,ProductFetchService productFetchService, ProductLinker productLinker) {
        this.productFetchService = productFetchService;
        this.productService = productService;
        this.productLinker = productLinker;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody
    List<Resource<Product>> getAllActiveProducts(){
        return productLinker.listToResource.apply(productFetchService.getAllActiveProducts());
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody
    Resource<Product> addProduct(@RequestBody Product newProduct){
        Long productId = productService.addProduct(newProduct);
        return productLinker.optionalToResource.apply(productFetchService.getProductById(productId));
    }

    @RequestMapping(value = "/{productId}", method = RequestMethod.GET)
    public @ResponseBody
    Resource<Product> getProductById(@PathVariable Long productId){
        return productLinker.optionalToResource.apply(productFetchService.getProductById(productId));
    }

    @RequestMapping(value = "/edit/{productId}", method = RequestMethod.PUT)
    public @ResponseBody
    Resource<Product> updateProductById(@PathVariable Long productId, @RequestBody Product updatedProduct){
        return productLinker.optionalToResource.apply(productService.updateProductById(productId, updatedProduct));
    }

    @RequestMapping(value = "/delete", method = RequestMethod.PUT)
    public @ResponseBody
    Resource<Boolean> deleteProductById(@RequestParam(value = "productId") Long productId){
        return new Resource<>(productService.deleteProductById(productId));
    }


    @RequestMapping(value = "/pictureUpload", method = RequestMethod.POST)
    public @ResponseBody
    void uploadPicture(@RequestPart("metadata") Map<String, Object> metadata,
                       @RequestPart("multipartFile") MultipartFile multipartFile) throws IOException {

        byte[] decodedBytes = java.util.Base64.getDecoder().decode(
                new String(multipartFile.getBytes(), Charset.forName("UTF-8")).split(",")[1].getBytes(Charset.forName("UTF-8")));
        System.out.println(decodedBytes);

    }

}
