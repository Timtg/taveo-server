package no.timesaver.dao;

import no.timesaver.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserCreatorJdbcDao implements UserCreatorDao {

    private final JdbcTemplate template;
    private final SimpleJdbcInsert userInsertActor;

    @Autowired
    public UserCreatorJdbcDao(JdbcTemplate jdbcTemplate) {
        this.template = jdbcTemplate;
        this.userInsertActor = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
    }




    @Override
    public boolean canBeCreated(String email, String mobile) {
        String sql = "Select count(id) as count from users where UPPER(email) = ? or mobile = ?";
        return template.queryForObject(sql,(rs, rowNum) -> rs.getLong("count"),email.toUpperCase(),mobile) == 0;
    }

    @Override
    public Number createNew(User userInfo) {
        Map<String,Object> insertParams = new HashMap<>();
        insertParams.put("email",userInfo.getEmail());
        insertParams.put("deleted", false);
        insertParams.put("name",userInfo.getName());
        insertParams.put("mobile",userInfo.getMobile());
        insertParams.put("type",userInfo.getType().name());
        insertParams.put("password_hash",userInfo.getPasswordHash());
        insertParams.put("accepted_disclaimer",userInfo.isAcceptedDisclaimer());
        insertParams.put("email_verified",userInfo.isEmailVerified());
        insertParams.put("mobile_verified",userInfo.isMobileVerified());
        insertParams.put("store_id",userInfo.getStoreId() == null || userInfo.getStoreId().equals(0L) ? null : userInfo.getStoreId());

        return userInsertActor.executeAndReturnKey(insertParams);
    }
}
