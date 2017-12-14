package no.timesaver.dao;

import no.timesaver.dao.mapper.ProductMapper;
import no.timesaver.domain.ReceiptProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Repository
public class ReceiptProductJdbcDao implements ReceiptProductDao {

    private final JdbcTemplate template;
    private final ProductMapper productMapper;

    @Autowired
    public ReceiptProductJdbcDao(JdbcTemplate jdbcTemplate, ProductMapper productMapper){
        this.template = jdbcTemplate;
        this.productMapper = productMapper;
    }


    @Override
    public Map<Long, List<ReceiptProduct>> getProductsForReceiptIds(Set<Long> receiptIds, Long storeId) {
        final Map<Long, List<ReceiptProduct>> map = new HashMap<>();
        if(receiptIds == null || receiptIds.isEmpty()) {
            return map;
        }
        StringJoiner joiner = new StringJoiner(",","(",")");
        receiptIds.forEach(id -> joiner.add(""+id));
        String sql = "Select rp.*,p.*,s.name as storeName " +
                "from receipt_products rp, product p, stores s " +
                "where s.id = p.store_id and p.id = rp.product_id " +
                "and receipt_id in " + joiner.toString();
        if (storeId != null){
            sql += "AND p.store_id = ?";
            template.query(sql,(rs, rowNum) -> mapReceiptProductsForReceipts(rs,map), storeId);
        }
        else {
            template.query(sql, (rs, rowNum) -> mapReceiptProductsForReceipts(rs, map));
        }
        return map;
    }

    private Map<Long, List<ReceiptProduct>> mapReceiptProductsForReceipts(ResultSet rs, Map<Long, List<ReceiptProduct>> map) throws SQLException{
        ReceiptProduct rp = mapReceiptProduct(rs);

        List<ReceiptProduct> list;
        if(map.containsKey(rp.getReceiptId())){
            list = map.get(rp.getReceiptId());
        } else {
            list = new ArrayList<>();
        }
        list.add(rp);
        map.put(rp.getReceiptId(),list);
        return map;
    }

    private ReceiptProduct mapReceiptProduct(ResultSet rs) throws SQLException {
        ReceiptProduct rp = new ReceiptProduct();
        rp.setReceiptId(rs.getLong("Receipt_Id"));
        rp.setCount(rs.getLong("Product_Count"));
        rp.setPrice(BigDecimal.valueOf(rs.getDouble("price")));
        rp.setProduct(productMapper.map(rs));
        return rp;
    }
}
