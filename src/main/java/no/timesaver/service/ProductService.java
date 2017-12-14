package no.timesaver.service;

import no.timesaver.dao.ProductDao;
import no.timesaver.domain.Product;
import no.timesaver.domain.User;
import no.timesaver.exception.InternalServerException;
import no.timesaver.security.AccessValidationService;
import no.timesaver.service.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private final ProductDao productDao;
    private final ProductPriceService priceService;
    private final AccessValidationService accessValidationService;
    private final CurrentUserService currentUserService;
    private final ProductFetchService productFetchService;

    @Autowired
    public ProductService(ProductDao productDao, ProductPriceService priceService, AccessValidationService accessValidationService, CurrentUserService currentUserService, ProductFetchService productFetchService) {
        this.productDao = productDao;
        this.priceService = priceService;
        this.accessValidationService = accessValidationService;
        this.currentUserService = currentUserService;
        this.productFetchService = productFetchService;
    }

    public Optional<Product> updateProductById(Long productId, Product product) {
        Optional<Product> originalProduct = productFetchService.getProductById(productId);
        if (!originalProduct.isPresent()){
            throw new IllegalArgumentException("Trying to update an product that doesn't exist");
        }
        User cu = currentUserService.getCurrentUser().orElseThrow(() -> new IllegalStateException("Missing current user for editing a product"));
        if(!accessValidationService.canEditProductForStore(cu, originalProduct.get().getStoreId())){
            throw new SecurityException("The current user is not allowed to edit the specified product");
        }

        if (originalProduct.get().getPrice().compareTo(product.getPrice()) != 0){
            priceService.changePrice(productId,product.getPrice());
        }

        if (productDao.updateProduct(productId,product)){
            return productFetchService.getProductById(productId);
        }
        else {
            return Optional.empty();
        }
    }

    public Boolean deleteProductById(Long productId) {
        Optional<Product> originalProduct = productFetchService.getProductById(productId);
        if (!originalProduct.isPresent()){
            throw new IllegalArgumentException("Trying to delete an product that doesn't exist");
        }
        User cu = currentUserService.getCurrentUser().orElseThrow(() -> new IllegalStateException("Missing current user for deleting a product"));
        if(!accessValidationService.canEditProductForStore(cu, originalProduct.get().getStoreId())){
            throw new SecurityException("The current user is not allowed to delete the specified product");
        }

        return productDao.deleteProduct(productId);
    }

    public Long addProduct(Product product) {
        User cu = currentUserService.getCurrentUser().orElseThrow(() -> new IllegalStateException("Missing current user for adding a product"));
        if(!accessValidationService.canEditProductForStore(cu, product.getStoreId())){
            throw new SecurityException("The current user is not allowed to edit the specified product");
        }
        Long productId = productDao.addProduct(product);
        productDao.addNewProductToProductPrice(product, productId);
        return productId;
    }
}
