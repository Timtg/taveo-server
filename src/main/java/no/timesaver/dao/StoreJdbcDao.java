package no.timesaver.dao;

import no.timesaver.domain.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class StoreJdbcDao implements StoreDao {

    private final static Logger log = LoggerFactory.getLogger(StoreJdbcDao.class);


    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertActor;

    @Autowired
    public StoreJdbcDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
        this.insertActor = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("stores")
                .usingGeneratedKeyColumns("id")
                .usingColumns("Franchise_Id", "Name", "Created", "Valid_From", "Valid_To", "Org_Number", "Longitude", "Latitude","icon_src","phone","address","email");
    }

    @Override
    public Optional<Store> getStoreById(Long storeId) {

        String sql ="Select Id, Franchise_Id, Name, Created, Valid_From, Valid_To, Org_Number, Longitude, Latitude,icon_src,phone,address,email" +
                " FROM stores where id = ?";

        try {
            Store store = jdbcTemplate.queryForObject(sql, new Object[]{storeId}, (rs, rowNum) -> mapStore(rs));
            return Optional.of(store);
        } catch (IncorrectResultSizeDataAccessException ie) {
            return Optional.empty();
        }

    }

    @Override
    public List<Store> getStoresByFranchiseId(Long franchiseId) {
        String sql ="SELECT Id, Franchise_Id, Name, Created, Valid_From, Valid_To, Org_Number, Longitude, Latitude,icon_src,phone,address,email" +
                " FROM stores WHERE Franchise_Id = ?";

        return jdbcTemplate.query(sql, new Object[]{franchiseId}, (rs, rowNum) -> mapStore(rs));
    }

    @Override
    public boolean updateStore(Store update) {
        String sql ="Update stores set Valid_to=?, valid_from=?, latitude=?, longitude=?,icon_src=? where id = ?";
        try {
            jdbcTemplate.update(sql,update.getValidTo(),update.getValidFrom(),update.getLatitude(), update.getLongitude(), update.getId());
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteStoreById(Long storeId) {
        //TODO
        return true;
    }

    @Override
    public List<Store> getAllActiveStores() {
        String sql ="SELECT Id, Franchise_Id, Name, Created, Valid_From, Valid_To, Org_Number, Longitude, Latitude,icon_src,phone,address,email " +
                " FROM stores WHERE "+getValidNowSql("",false);

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapStore(rs));
    }

    @Override
    public Set<String> getStoreNameForIds(List<Long> storeIds) {
        if(storeIds == null || storeIds.isEmpty()){
            return new HashSet<>();
        }

        StringJoiner joiner = new StringJoiner(",","(",")");
        storeIds.forEach(id -> joiner.add(""+id));

        String sql = "Select name from Stores where id in "+ joiner.toString();
        List<String> names = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("Name"));
        return names.stream().collect(Collectors.toSet());
    }

    @Override
    public Long add(Long franchiseId, String storeName, int storeOrgNumber, BigDecimal storeLongitude, BigDecimal storeLatitude, String storeIconSrc, String storeContactPhone, String storeAddress, String storeContactEmail) {
        Map<String,Object> insertParams = new HashMap<>();
        insertParams.put("Franchise_Id",franchiseId);
        insertParams.put("name",storeName);
        insertParams.put("Created",Timestamp.valueOf(LocalDateTime.now()));
        insertParams.put("Valid_From",Timestamp.valueOf(LocalDateTime.now()));
        insertParams.put("Valid_To", Timestamp.valueOf(LocalDateTime.now().plusMonths(3)));
        insertParams.put("Org_Number",storeOrgNumber);
        insertParams.put("Longitude",storeLongitude);
        insertParams.put("Latitude",storeLatitude);
        insertParams.put("icon_src",storeIconSrc);
        insertParams.put("phone",storeContactPhone);
        insertParams.put("address",storeAddress);
        insertParams.put("email",storeContactEmail);


        Number id = insertActor.executeAndReturnKey(insertParams);
        log.info("Added {} to stores", storeName);
        return id.longValue();
    }

    private Store mapStore( ResultSet rs) throws SQLException {
        Store store = new Store(rs.getLong("Id"));
        store.setFranchiseId(rs.getLong("Franchise_Id"));
        store.setName(rs.getString("Name"));
        store.setCreated(LocalDateTime.ofInstant(rs.getTimestamp("Created").toInstant(), ZoneId.systemDefault()));
        store.setValidFrom(LocalDateTime.ofInstant(rs.getTimestamp("Valid_From").toInstant(), ZoneId.systemDefault()));
        store.setValidTo(LocalDateTime.ofInstant(rs.getTimestamp("Valid_To").toInstant(), ZoneId.systemDefault()));
        store.setOrgNumber(rs.getLong("Org_Number"));
        store.setLatitude(rs.getBigDecimal("Latitude"));
        store.setLongitude(rs.getBigDecimal("Latitude"));
        store.setIconSrc(rs.getString("icon_src"));
        store.setPhone(rs.getString("phone"));
        store.setAddress(rs.getString("address"));
        store.setEmail(rs.getString("email"));

        return store;
    }
}
