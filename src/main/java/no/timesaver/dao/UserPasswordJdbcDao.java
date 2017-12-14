package no.timesaver.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class UserPasswordJdbcDao implements UserPasswordDao {

    private static final Logger log = LoggerFactory.getLogger(UserPasswordJdbcDao.class);
    private final JdbcTemplate template;


    @Autowired
    public UserPasswordJdbcDao(JdbcTemplate jdbcTemplate) {
        this.template = jdbcTemplate;
    }


    @Override
    public Optional<String> getHashForUserByEmail(String email) {
        try{
            return Optional.of(template.queryForObject("Select password_hash from users where email = ?",(rs, rowNum) -> rs.getString("password_hash"),email));
        } catch (IncorrectResultSizeDataAccessException e) {
            log.warn("Unable to get passwordHash for user with email: {}, no userEntry found", email);
            return Optional.empty();
        }
    }

    @Override
    public void setResetInfo(Long id, String otpResetHash, LocalDateTime resetValidTo) {
        String sql = "UPDATE users set reset_confirmation_hash=?,reset_code_valid_to=? where id =?";
        template.update(sql,otpResetHash, Timestamp.valueOf(resetValidTo),id);
    }

    @Override
    public void resetPassword(Long id, String hash) {
        String sql = "UPDATE users set password_hash=?,reset_code_valid_to=null,reset_confirmation_hash=null where id =?";
        template.update(sql,hash,id);
    }

    @Override
    public Optional<String> getResetCodeHashIfValid(Long userId) {
        String sql = "SELECT reset_confirmation_hash from users WHERE id = ? and reset_code_valid_to >= current_timestamp";
        try {
            return Optional.of(template.queryForObject(sql,(rs, rowNum) -> rs.getString("reset_confirmation_hash"),userId));
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }
}
