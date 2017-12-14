package no.timesaver.api.v1.controller;

import no.timesaver.service.ProductPriceService;
import no.timesaver.tools.DateTimeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(value ="/api/v1/product/price")
public class ProductPriceController {


    private final ProductPriceService productPriceService;
    private final DateTimeConverter dateTimeConverter;

    @Autowired
    public ProductPriceController(ProductPriceService productPriceService, DateTimeConverter dateTimeConverter) {
        this.productPriceService = productPriceService;
        this.dateTimeConverter = dateTimeConverter;
    }

    @RequestMapping(value = "/{productId}/interval", method = RequestMethod.PUT)
    public @ResponseBody
    Boolean updatePriceForProductForAnInterval(
            @PathVariable Long productId,
            @RequestParam(value = "newPrice") BigDecimal newPrice,
            @RequestParam(value = "validTo")String validTo,
            @RequestParam(value = "revertToPrice")String revertToPrice
            ){
        return productPriceService.changePrice(productId,newPrice,dateTimeConverter.toLocalDateTime(validTo.replaceAll(" ","+")),new BigDecimal(revertToPrice));
    }

    @RequestMapping(value = "/{productId}", method = RequestMethod.PUT)
    public @ResponseBody
    boolean updatePriceForProduct(@PathVariable Long productId,@RequestParam(value = "newPrice") BigDecimal newPrice){
        return productPriceService.changePrice(productId,newPrice);
    }

    @RequestMapping(value = "/{productId}", method = RequestMethod.GET)
    public @ResponseBody
    BigDecimal getPriceForProductById(@PathVariable Long productId){
        return productPriceService.getPriceForProductById(productId).orElseThrow(() -> new IllegalArgumentException("Unable to find price for product with id " + productId));
    }
}
