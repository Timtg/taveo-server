package no.timesaver.dao;


import no.timesaver.domain.PushNotificationInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class PushNotificationJdbcDao implements PushNotificationDao{
    private final static Logger log = LoggerFactory.getLogger(PushNotificationJdbcDao.class);

    private final JdbcTemplate template;

    private final int MAX_DEVICED_PER_USER = 5;

    @Autowired
    public PushNotificationJdbcDao(JdbcTemplate template) {
        this.template = template;
    }


    @Override
    public void registerNewDeviceForUser(Long userId, String token, String system) {
        String insertSql = "Insert Into push_notification_tokens (user_id, push_token, system, created) VALUES (?,?,?,current_timestamp) ON CONFLICT DO NOTHING";
        int updateCount = template.update(insertSql, userId, token, system);
        if(updateCount == 0){
            try {
                updateCount = template.update("UPDATE push_notification_tokens set user_id=?,created=current_timestamp,system=? where push_token=?",userId,system,token );
            } catch (DataAccessException e) {
                updateCount = 0;
            }
        }

        if(updateCount == 0){
            log.warn("Unable to insert or update push information userId: {}, system: {}, token: {}",userId,system,token);
        }

        enforceMaxEntriesPerUser(userId);
    }

    @Override
    public List<PushNotificationInformation> getPushInformationForUserId(Long userId) {
        String sql = "SELECT user_id,push_token,system from push_notification_tokens WHERE user_id = ?";
        return template.query(sql,(rs, rowNum) -> mapPushInformation(rs),userId);

    }

    private PushNotificationInformation mapPushInformation(ResultSet rs) throws SQLException {
        PushNotificationInformation i = new PushNotificationInformation();
        i.setSystem(rs.getString("system"));
        i.setUserId(rs.getLong("user_Id"));
        i.setToken(rs.getString("push_token"));
        return i;
    }

    private void enforceMaxEntriesPerUser(Long userId) {
        String countSql = "Select created from push_notification_tokens where user_id = ? ORDER BY created DESC";
        List<Timestamp> created = template.query(countSql, (rs, rowNum) -> rs.getTimestamp("created"), userId);

        if(created.size() > MAX_DEVICED_PER_USER){
            Timestamp earliest = created.stream().findFirst().orElseThrow(() -> new IllegalStateException("WARNING! This exception should never be thrown! This is an illegal state"));
            template.update("Delete from push_notification_tokens where created = ? and user_id =?",earliest,userId);
            log.info("Removed oldest pushNotification entry for user {} with created-timestamp {] as there were more than {} entries for the user",userId,earliest,MAX_DEVICED_PER_USER);
        }

    }
}
