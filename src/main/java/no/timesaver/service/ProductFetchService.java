package no.timesaver.service;

import no.timesaver.dao.ProductDao;
import no.timesaver.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductFetchService {

    private final ProductDao productDao;

    @Autowired
    public ProductFetchService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public Optional<Product> getProductById(Long productId) {
        return productDao.getProductById(productId);
    }

    public List<Product> getProductsForStoreById(Long storeId) {
        return productDao.getProductsForStoreById(storeId).stream()
                .filter(p -> p.getPrice() != null).collect(Collectors.toList());
    }

    public List<Product> getAllActiveProducts() {
        return productDao.getAllActiveProducts();
    }

    public List<Product> getActiveProductsByIds(Set<Long> ids) {
        return productDao.getActiveProductsByIds(ids);
    }
}
