package no.timesaver.dao;

import no.timesaver.domain.Advertisement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class AdsJdbcDao implements AdsDao {

    private final static Logger log = LoggerFactory.getLogger(AdsJdbcDao.class);
    private final JdbcTemplate template;

    @Autowired
    public AdsJdbcDao(JdbcTemplate jdbcTemplate) {
        this.template = jdbcTemplate;
    }


    @Override
    public List<Advertisement> getAllActiveAdsWithoutProductInfo() {
        String sql = "SELECT * from advertisement where product_id is not null " +getValidNowSql(null);
        return template.query(sql,(rs,rowNum)-> mapAdvertisement(rs));
    }

    @Override
    public Optional<Advertisement> getAdWithoutProductById(Long productId) {
        String sql = "SELECT * from advertisement where product_id =?";
        try {
            return template.queryForObject(sql,(rs,rowNum)-> Optional.of(mapAdvertisement(rs)),productId);
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error("Unable to find product with id {}",productId);
            return Optional.empty();
        }
    }

    private Advertisement mapAdvertisement(ResultSet rs) throws SQLException {
        Advertisement ad = new Advertisement();
        ad.setId(rs.getLong("id"));
        ad.setProductId(rs.getLong("product_id"));
        ad.setValidFrom(rs.getTimestamp("valid_from").toLocalDateTime());
        ad.setValidTo(rs.getTimestamp("valid_to").toLocalDateTime());
        return ad;
    }
}
