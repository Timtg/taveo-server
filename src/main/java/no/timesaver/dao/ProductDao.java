package no.timesaver.dao;

import no.timesaver.domain.Product;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductDao extends AbstractDao{
    Optional<Product> getProductById(Long productId);

    List<Product> getAllActiveProducts();

    List<Product> getProductsForStoreById(Long storeId);

    List<Product> getActiveProductsByIds(Set<Long> ids);

    boolean changePrice(Long productId, BigDecimal newPrice);

    boolean updateProduct(Long productId, Product product);

    Boolean deleteProduct(Long productId);

    Long addProduct(Product product);

    void changePrice(Long productId, BigDecimal newPrice, LocalDateTime validTo, BigDecimal revertToPriceAfterValidTo);

    Optional<Pair<LocalDateTime,LocalDateTime>> getOfferForProduct(Long productId);

    void addNewProductToProductPrice(Product product, Long productId);
}
