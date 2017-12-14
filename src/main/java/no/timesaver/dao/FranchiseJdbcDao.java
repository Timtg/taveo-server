package no.timesaver.dao;

import no.timesaver.domain.Franchise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class FranchiseJdbcDao implements FranchiseDao {

    private final static Logger log = LoggerFactory.getLogger(FranchiseJdbcDao.class);

    private final JdbcTemplate template;
    private final SimpleJdbcInsert insertActor;

    @Autowired
    public FranchiseJdbcDao(JdbcTemplate jdbcTemplate){
        this.template = jdbcTemplate;
        this.insertActor = new SimpleJdbcInsert(template)
                .withTableName("franchise")
                .usingGeneratedKeyColumns("id")
                .usingColumns("name", "created");
    }




    @Override
    public Optional<Franchise> getById(Long franchiseId) {
        String sql = "Select * from franchise where id = ?";
        try {
            return Optional.of(template.queryForObject(sql,(rs, rowNum) -> mapFranchise(rs),franchiseId));
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Long add(String franchiseName) {
        Map<String,Object> insertParams = new HashMap<>();
        insertParams.put("name",franchiseName);
        insertParams.put("created", Timestamp.valueOf(LocalDateTime.now()));

        Number id = insertActor.executeAndReturnKey(insertParams);
        log.info("Added {} to franchises", franchiseName);
        return id.longValue();
    }

    private  Franchise mapFranchise(ResultSet rs) throws SQLException {
        Franchise f = new Franchise();
        f.setName(rs.getString("name"));
        f.setId(rs.getLong("id"));
        f.setCreated(rs.getTimestamp("created").toLocalDateTime());
        return f;
    }
}
