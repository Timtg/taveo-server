package no.timesaver.dao.mapper;

import no.timesaver.domain.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ProductMapper {

    public Product map(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setStoreId(rs.getLong("Store_Id"));
        p.setStoreName(rs.getString("storeName"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("Description"));
        p.setGroup(rs.getString("group"));
        p.setValidFrom(rs.getTimestamp("Valid_From").toLocalDateTime());
        p.setValidTo(rs.getTimestamp("Valid_To").toLocalDateTime());
        p.setIconSrc(rs.getString("icon_src"));
        p.setPrice(BigDecimal.valueOf(rs.getDouble("price")));
        return p;
    }
}
