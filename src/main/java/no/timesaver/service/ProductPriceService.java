package no.timesaver.service;

import no.timesaver.domain.User;
import no.timesaver.exception.InternalServerException;
import no.timesaver.security.AccessValidationService;
import no.timesaver.service.user.CurrentUserService;
import org.apache.commons.lang3.tuple.Pair;
import no.timesaver.dao.ProductDao;
import no.timesaver.domain.Product;
import no.timesaver.tools.ObjectValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductPriceService {

    private final ObjectValidator objectValidator;
    private final ProductDao productDao;
    private final ProductFetchService productFetchService;
    private final CurrentUserService currentUserService;
    private final AccessValidationService accessValidationService;


    @Autowired
    public ProductPriceService(ObjectValidator objectValidator, ProductDao productDao, ProductFetchService productFetchService, CurrentUserService currentUserService, AccessValidationService accessValidationService) {
        this.objectValidator = objectValidator;
        this.productDao = productDao;
        this.productFetchService = productFetchService;
        this.currentUserService = currentUserService;
        this.accessValidationService = accessValidationService;
    }

    /***
     NB!
     NB!
     Very important when updating the pricee for an existing product that there will be no overlap in the validation periodes!!
     NB!
     */


    /*Change the existing price, for the current time period*/
    public boolean changePrice(Long productId, BigDecimal newPrice){
        currentUserCanChangePriceForProduct(productId);
        return productDao.changePrice(productId,newPrice);
    }


    public boolean changePrice(Long productId, BigDecimal newPrice, LocalDateTime validTo, BigDecimal revertToPriceAfterValidTo){
        currentUserCanChangePriceForProduct(productId);

        if(!objectValidator.isValid(Arrays.asList(productId,newPrice,validTo,revertToPriceAfterValidTo))){
            throw new IllegalArgumentException("Arguments cannot be null, 0 or empty");
        }
        if (!overlapsWithExistingOffer(productId,validTo)){
            productDao.changePrice(productId, newPrice, validTo, revertToPriceAfterValidTo);
            return true;
        }
        return false;
    }

    private void currentUserCanChangePriceForProduct(Long productId) {
        Optional<Product> originalProduct = productFetchService.getProductById(productId);
        if (!originalProduct.isPresent()){
            throw new InternalServerException("Trying to change the price an product that doesn't exist");
        }
        User cu = currentUserService.getCurrentUser().orElseThrow(() -> new IllegalStateException("Missing current user for changing the price for a product"));
        if(!accessValidationService.canEditProductForStore(cu, originalProduct.get().getStoreId())){
            throw new SecurityException("The current user is not allowed to change the price the specified product");
        }
    }

    boolean overlapsWithExistingOffer(Long productId, LocalDateTime validTo) {
        Optional<Pair<LocalDateTime, LocalDateTime>> timeRangeForProduct = productDao.getOfferForProduct(productId);
        if (timeRangeForProduct.isPresent()) {
            if (timeRangeForProduct.get().getLeft().isBefore(validTo) && LocalDateTime.now().isBefore(timeRangeForProduct.get().getRight())) {
                return true;
            }
        }
        return false;
    }


    public Optional<BigDecimal> getPriceForProductById(Long productId) {
        Optional<Product> productById = productDao.getProductById(productId);
        if(!productById.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(productById.get().getPrice());
    }

    Map<Long, BigDecimal> getPriceForProductsByIds(Set<Long> productIds) {
        List<Product> productsByIds = productDao.getActiveProductsByIds(productIds);
        return productsByIds.stream().collect(Collectors.toMap(Product::getId, Product::getPrice));
    }
}
