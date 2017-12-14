package no.timesaver.dao;

import no.timesaver.domain.StoreSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class StoreSettingsJdbcDao implements StoreSettingsDao {

    private final JdbcTemplate template;
    private final SimpleJdbcInsert settingsInsertActor;

    @Autowired
    public StoreSettingsJdbcDao(JdbcTemplate template) {
        this.template = template;
        this.settingsInsertActor = new SimpleJdbcInsert(template)
                .withTableName("store_settings");

    }

    @Override
    public Optional<StoreSettings> getSettingsForStore(Long storeId) {
        String sql = "SELECT * from store_settings where store_id = ?";
        try {
            return Optional.of(template.queryForObject(sql,((rs, rowNum) -> mapStoreSettings(rs)),storeId));
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Map<Long, StoreSettings> getSettingsForStores(Set<Long> storeIds) {
        Map<Long, StoreSettings> map = new HashMap<>();
        if(storeIds == null ||storeIds.isEmpty()){
            return map;
        }

        StringJoiner joiner = new StringJoiner(",","(",")");
        storeIds.forEach(id -> joiner.add(""+id));

        String sql = "SELECT * from store_settings where store_id in " + joiner.toString();
        template.query(sql,(rs, rowNum) -> map.put(rs.getLong("Store_Id"),mapStoreSettings(rs)));
        return map;
    }

    @Override
    public void updateStoreSettings(StoreSettings storeSettings) {
        String sql = "UPDATE store_settings set manual_time_verification =?,automatic_print_dialog=? where store_id=?";
        template.update(sql,storeSettings.getManualTimeVerification(),storeSettings.getAutomaticPrintDialog(),storeSettings.getStoreId());
    }

    @Override
    public List<Long> getStoreIdsForWhichTimeVerificationIsRequired(List<Long> storeIds) {
        if(storeIds.isEmpty()){
            return Collections.emptyList();
        }

        StringJoiner joiner = new StringJoiner(",","(",")");
        storeIds.forEach(id -> joiner.add(""+id));

        String sql = "SELECT store_id from store_settings WHERE manual_time_verification = TRUE and store_id in " + joiner.toString();
        return template.query(sql,(rs, rowNum) -> rs.getLong("store_Id"));
    }

    @Override
    public void createStoreSettings(StoreSettings storeSettings) {
        Map<String,Object> insertParams = new HashMap<>();
        insertParams.put("Store_Id",storeSettings.getStoreId());
        insertParams.put("Manual_Time_verification", storeSettings.getManualTimeVerification());
        insertParams.put("Automatic_Print_Dialog",storeSettings.getAutomaticPrintDialog());

        settingsInsertActor.execute(insertParams);
    }


    private StoreSettings mapStoreSettings(ResultSet rs) throws SQLException {
        StoreSettings ss = new StoreSettings();
        ss.setStoreId(rs.getLong("Store_Id"));
        ss.setAutomaticPrintDialog(rs.getBoolean("Automatic_Print_Dialog"));
        ss.setManualTimeVerification(rs.getBoolean("Manual_Time_verification"));
        return ss;
    }
}
