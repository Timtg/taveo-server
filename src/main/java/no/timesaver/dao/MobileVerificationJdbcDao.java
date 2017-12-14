package no.timesaver.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MobileVerificationJdbcDao implements MobileVerificationDao {

    private final JdbcTemplate template;

    @Autowired
    public MobileVerificationJdbcDao(JdbcTemplate jdbcTemplate) {
        this.template = jdbcTemplate;
    }


    @Override
    public Optional<String> getVerificationHashForUser(long userId) {
        String sql ="Select verification_code_hash from Mobile_verification where user_id =?";
        try {
            return Optional.of(template.queryForObject(sql,(rs, rowNum) -> rs.getString("verification_code_hash"),userId));
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean removeVerificationEntryByUserId(long userId) {
        return template.update("DELETE FROM Mobile_verification where user_id = ?",userId) == 1;
    }

    @Override
    public void setMobileVerifiedForUser(Long userId) {
        template.update("UPDATE users SET mobile_verified=? where id =?",true,userId);
    }

    @Override
    public void addVerificationEntryForUserId(long userId, String hash) {
        int update = template.update("INSERT INTO Mobile_verification (user_id, verification_code_hash) VALUES (?,?) ON CONFLICT DO NOTHING ", userId, hash);
        if(update != 1){
            template.update("UPDATE Mobile_verification set verification_code_hash = ? where user_id =?",hash,userId);
        }

    }
}
