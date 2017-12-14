package no.timesaver.dao;

import no.timesaver.dao.mapper.ProductMapper;
import no.timesaver.domain.Product;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;


@Repository
public class ProductJdbcDao implements ProductDao {

    private final JdbcTemplate template;
    private final static Logger log = LoggerFactory.getLogger(ProductJdbcDao.class);
    private final SimpleJdbcInsert productInsertActor;
    private final SimpleJdbcInsert productPriceInsertActor;



//    private final String validPriceSubQuery = " (" +
//            "  SELECT price,product_id" +
//            "  FROM product_price" +
//            "  WHERE valid_from <= current_timestamp AT time zone '"+getShortTimeZone()+"' AND valid_to >= current_timestamp AT time zone '"+getShortTimeZone()+"'"+
//            ") as validPrice ";

    private final String validPriceSubQuery = " (" +
            "  SELECT price,product_id" +
            "  FROM product_price" +
            "  WHERE valid_from <= current_timestamp AND valid_to >= current_timestamp "+
            ") as validPrice ";
    private final ProductMapper productMapper;

    @Autowired
    public ProductJdbcDao(JdbcTemplate jdbcTemplate, ProductMapper productMapper) {
        this.template = jdbcTemplate;
        this.productMapper = productMapper;
        this.productInsertActor = new SimpleJdbcInsert(template)
                .withTableName("product")
                .usingGeneratedKeyColumns("id")
                .usingColumns("store_id", "name", "description", "\"Group\"",
                        "valid_from", "valid_to", "icon_src");
        this.productPriceInsertActor = new SimpleJdbcInsert(template)
                .withTableName("product_price")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<Product> getProductById(Long productId) {
        String sql = "Select p.*,s.name as storeName,validPrice.price from stores s ,product p, " + validPriceSubQuery +
                " where validPrice.product_id = p.id and p.id = ? and p.store_id = s.id " +
                getValidNowSql("p");
        try {
            return Optional.of(template.queryForObject(sql,(rs,rowNum) -> productMapper.map(rs),productId));
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Product> getAllActiveProducts() {
        String sql = "Select p.*,s.name as storeName,validPrice.price from stores s ,product p, " + validPriceSubQuery +
                " where validPrice.product_id = p.id and deleted = FALSE and p.id = ? and p.store_id = s.id " +
                getValidNowSql("p") +" " + getValidNowSql("s");

        return template.query(sql,(rs,rowNum) -> productMapper.map(rs));

    }

    @Override
    public List<Product> getProductsForStoreById(Long storeId) {
        String sql = "Select p.*,s.name as storeName,validPrice.price from stores s ,product p, " + validPriceSubQuery +
                " where validPrice.product_id = p.id and p.store_id = ? and p.store_id = s.id and deleted = FALSE " +
                getValidNowSql("p");
        return template.query(sql,(rs,rowNum)-> productMapper.map(rs),storeId);
    }

    @Override
    public List<Product> getActiveProductsByIds(Set<Long> ids) {
        if(ids == null || ids.isEmpty()){
            return Collections.emptyList();
        }
        StringJoiner joiner = new StringJoiner(",","(",")");
        ids.forEach(id -> joiner.add(""+id));

        String sql = "Select p.*,s.name as storeName,validPrice.price from stores s ,product p, " + validPriceSubQuery +
                " where validPrice.product_id = p.id and p.store_id = s.id and deleted = FALSE and p.id in " + joiner.toString() +" " +
                getValidNowSql("p");
        return template.query(sql,(rs,rowNum)-> productMapper.map(rs));
    }

    @Override
    public boolean changePrice(Long productId, BigDecimal newPrice) {
        String sql ="UPDATE product_price set price = ? WHERE product_id = ?;";
        try {
            template.update(sql,newPrice,productId);
        } catch (DataAccessException e) {
            return false;
        }
        return true;

    }

    @Override
    public boolean updateProduct(Long productId, Product product) {
        String sql ="UPDATE product SET name = ?, description = ?, \"Group\" = ? WHERE id = ?";

        try {
            template.update(sql,product.getName(), product.getDescription(), product.getGroup(), productId);
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    @Override
    public Boolean deleteProduct(Long productId) {
        String sql ="UPDATE Product set deleted = true where id = ?";
        try{
            template.update(sql, productId);
        }
        catch (DataAccessException e){
            log.error("failed to delete product with product ID {}, error: {}", productId, e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public Long addProduct(Product product) {
        Map<String,Object> insertParams = new HashMap<>();
        insertParams.put("store_id",product.getStoreId());
        insertParams.put("name",product.getName());
        insertParams.put("description", product.getDescription());
        insertParams.put("\"Group\"", product.getGroup());
        insertParams.put("valid_from", Timestamp.valueOf(LocalDateTime.now().minusHours(2)));
        insertParams.put("valid_to",Timestamp.valueOf(LocalDateTime.now().plusYears(100)));
        insertParams.put("icon_src",product.getIconSrc());

        Number id = productInsertActor.executeAndReturnKey(insertParams);
        log.info("Added {} to products, for storeid {}", product.getName(), product.getStoreId());
        return id.longValue();
    }

    @Override
    public void changePrice(Long productId, BigDecimal newPrice, LocalDateTime validTo, BigDecimal revertToPriceAfterValidTo) {
        String sql ="INSERT INTO product_price (price, valid_from, valid_to, product_id) VALUES (?, current_timestamp, ?, ?);";
        template.update(sql, newPrice, Timestamp.valueOf(validTo), productId);

    }

    @Override
    public Optional<Pair<LocalDateTime, LocalDateTime>> getOfferForProduct(Long productId) {
        String sql ="SELECT valid_from, valid_to from product_price where product_id = ? ORDER BY valid_from DESC FETCH FIRST 1 ROW ONLY";
        try{
            return Optional.of(template.queryForObject(sql,  (rs, rowNum) -> Pair.of(rs.getTimestamp("valid_from").toLocalDateTime(), rs.getTimestamp("valid_to").toLocalDateTime()), productId));
        }
        catch (EmptyResultDataAccessException er){
            return Optional.empty();
        }
    }

    @Override
    public void addNewProductToProductPrice(Product product, Long productId) {
        Map<String,Object> insertParams = new HashMap<>();
        insertParams.put("price",product.getPrice());
        insertParams.put("valid_from", Timestamp.valueOf(LocalDateTime.now().minusHours(2)));
        insertParams.put("valid_to",Timestamp.valueOf(LocalDateTime.now().plusYears(100)));
        insertParams.put("product_id",productId);

        productPriceInsertActor.execute(insertParams);
    }
}
