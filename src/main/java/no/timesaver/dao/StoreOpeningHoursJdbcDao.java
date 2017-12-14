package no.timesaver.dao;

import no.timesaver.domain.StoreOpeningHours;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class StoreOpeningHoursJdbcDao implements StoreOpeningHoursDao {

    private final JdbcTemplate template;

    @Autowired
    public StoreOpeningHoursJdbcDao(JdbcTemplate template) {
        this.template = template;
    }


    @Override
    public Optional<StoreOpeningHours> getStoreOpeningHoursByStoreId(Long storeId) {
        String sql = "SELECT s.name,oh.* from Stores s,store_opening_hours oh where oh.store_id = s.id and s.id = ?";
        try {
            return Optional.of(template.queryForObject(sql,(rs, rowNum) -> mapOpeningHours(rs),storeId));
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void saveOrUpdate(StoreOpeningHours openingHours) {
        int update = template.update("INSERT INTO store_opening_hours " +
                        "(store_id, monday_start, monday_end," +
                        " tuesday_start, tuesday_end," +
                        " wednesday_start, wednesday_end," +
                        " thursday_start, thursday_end," +
                        " friday_start, friday_end," +
                        " saturday_start, saturday_end," +
                        " sunday_start, sunday_end) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT DO NOTHING ",
                openingHours.getStoreId(),openingHours.getMondayStart(),openingHours.getMondayEnd(),
                openingHours.getTuesdayStart(),openingHours.getTuesdayEnd(),
                openingHours.getWednesdayStart(),openingHours.getWednesdayEnd(),
                openingHours.getThursdayStart(),openingHours.getThursdayEnd(),
                openingHours.getFridayStart(),openingHours.getFridayEnd(),
                openingHours.getSaturdayStart(),openingHours.getSaturdayEnd(),
                openingHours.getSundayStart(),openingHours.getSundayEnd()
        );
        if(update != 1){
            template.update("UPDATE store_opening_hours set " +
                    " monday_start=?, monday_end=?," +
                    " tuesday_start=?, tuesday_end=?, " +
                    " wednesday_start=?, wednesday_end=?, " +
                    " thursday_start=?, thursday_end=?, " +
                    " friday_start=?, friday_end=?, " +
                    " saturday_start=?, saturday_end=?, " +
                    " sunday_start=?, sunday_end=? where store_id =?",
                    openingHours.getMondayStart(),openingHours.getMondayEnd(),
                    openingHours.getTuesdayStart(),openingHours.getTuesdayEnd(),
                    openingHours.getWednesdayStart(),openingHours.getWednesdayEnd(),
                    openingHours.getThursdayStart(),openingHours.getThursdayEnd(),
                    openingHours.getFridayStart(),openingHours.getFridayEnd(),
                    openingHours.getSaturdayStart(),openingHours.getSaturdayEnd(),
                    openingHours.getSundayStart(),openingHours.getSundayEnd(),
                    openingHours.getStoreId());
        }
    }

    private StoreOpeningHours mapOpeningHours(ResultSet rs) throws SQLException {
        StoreOpeningHours soh = new StoreOpeningHours();
        soh.setStoreId(rs.getLong("store_id"));
        soh.setStoreName(rs.getString("name"));
        soh.setMondayStart(rs.getTime("monday_start").toLocalTime());
        soh.setMondayEnd(rs.getTime("monday_end").toLocalTime());

        soh.setTuesdayStart(rs.getTime("tuesday_start").toLocalTime());
        soh.setTuesdayEnd(rs.getTime("tuesday_end").toLocalTime());

        soh.setWednesdayStart(rs.getTime("wednesday_start").toLocalTime());
        soh.setWednesdayEnd(rs.getTime("wednesday_end").toLocalTime());

        soh.setThursdayStart(rs.getTime("thursday_start").toLocalTime());
        soh.setThursdayEnd(rs.getTime("thursday_end").toLocalTime());

        soh.setFridayStart(rs.getTime("friday_start").toLocalTime());
        soh.setFridayEnd(rs.getTime("friday_end").toLocalTime());

        soh.setSaturdayStart(rs.getTime("saturday_start").toLocalTime());
        soh.setSaturdayEnd(rs.getTime("saturday_end").toLocalTime());

        soh.setSundayStart(rs.getTime("sunday_start").toLocalTime());
        soh.setSundayEnd(rs.getTime("sunday_end").toLocalTime());

        return soh;
    }
}
