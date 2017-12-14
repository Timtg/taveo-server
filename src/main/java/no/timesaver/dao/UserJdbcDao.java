package no.timesaver.dao;

import no.timesaver.domain.User;
import no.timesaver.domain.types.UserTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class UserJdbcDao implements UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserJdbcDao.class);
    private final JdbcTemplate template;


    @Autowired
    public UserJdbcDao(JdbcTemplate jdbcTemplate) {
        this.template = jdbcTemplate;
    }


    @Override
    public Optional<User> findById(Long userId) {
        String sql = "SELECT id,email,mobile,name,type,accepted_disclaimer,email_verified,mobile_verified,store_id from users where id = ?";
        try {
            return Optional.of(template.queryForObject(sql,(rs, rowNum) -> mapUser(rs),userId));
        } catch (IncorrectResultSizeDataAccessException e) {
            log.warn("Unable to find user with id {}", userId);
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getHashForUserByEmail(String email) {
        try{
            return Optional.of(template.queryForObject("Select password_hash from users WHERE UPPER(email) = ?",(rs, rowNum) -> rs.getString("password_hash"),email.toUpperCase()));
        } catch (IncorrectResultSizeDataAccessException e) {
            log.warn("Unable to get passwordHash for user with email: {}, no userEntry found", email);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id,email,mobile,name,type,accepted_disclaimer,email_verified,mobile_verified,store_id from users WHERE UPPER(email) = ?";
        try {
            return Optional.of(template.queryForObject(sql,(rs, rowNum) -> mapUser(rs),email.toUpperCase()));
        } catch (IncorrectResultSizeDataAccessException e) {
            log.warn("Unable to find user with email {}", email);
            return Optional.empty();
        }
    }

    @Override
    public boolean confirmDisclaimerForUserById(Long id) {
        String sql = "UPDATE users set accepted_disclaimer=true where id = ?";
        return template.update(sql, id) == 1;
    }

    @Override
    public Optional<Long> getStoreIdForUser(Long userId) {
        String sql = "SELECT store_id from users where id = ? AND type != 'N'";

        try {
            return Optional.of(template.queryForObject(sql, new Object[]{userId},(rs, rowNum) -> rs.getLong("Store_Id")));
        } catch (EmptyResultDataAccessException e) {
            log.warn("User with Id={} tried to login, but found no storeId for that user in DB", userId);
            return Optional.empty();
        }

    }

    @Override
    public Optional<List<User>> getUsersForStore(Long storeId) {
        String sql ="SELECT id,email,mobile,name,type,accepted_disclaimer,email_verified,mobile_verified,store_id " +
                "FROM users " +
                "where store_id = ? and type != 'A' AND deleted IS FALSE ";
        return Optional.of(template.query(sql, (rs, rowNum) -> mapUser(rs), storeId));
    }

    @Override
    public boolean deleteUserById(Long userId) {
        String sql = "update users set  deleted = TRUE where id = ?;";
        try{
            return template.update(sql, userId) == 1;
        }
        catch (IncorrectResultSizeDataAccessException ie){
            log.error("failed to delete user with id {}", userId);
            return false;
        }
    }

    @Override
    public boolean updateStoreUser(User user) {
        String sql ="update users set email = ?, name =?, mobile = ?, type = ? where id = ? ;";
        try {
            template.update(sql, user.getEmail(),user.getName(),user.getMobile(),user.getType().name(), user.getId());
            return true;
        }catch (IncorrectResultSizeDataAccessException ie){
                log.error("failed to update user with id {}", user.getId());
                return false;
            }

    }

    @Override
    public Optional<String> getUserNameById(Long userId) {
        String sql = "Select name from users WHERE  id = ?";
        try {
            return Optional.of(template.queryForObject(sql,(rs, rowNum) -> rs.getString("name"),userId));
        } catch (IncorrectResultSizeDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean userExists(Long userId) {
        String sql = "Select count(id) as count from users WHERE id = ?";
        return 1 == template.queryForObject(sql, (rs, rowNum) -> rs.getLong("count"),userId);
    }

    @Override
    public void setLastLogin(Long userId, LocalDateTime time) {
        String sql = "UPDATE users set last_login=? where id=?";
        template.update(sql, Timestamp.valueOf(time),userId);
    }

    private  User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setEmail(rs.getString("email"));
        u.setMobile(rs.getString("mobile"));
        u.setName(rs.getString("name"));
        u.setType(UserTypeEnum.valueOf(rs.getString("type").toUpperCase()));
        u.setAcceptedDisclaimer(rs.getBoolean("accepted_disclaimer"));
        u.setEmailVerified(rs.getBoolean("email_verified"));
        u.setMobileVerified(rs.getBoolean("mobile_verified"));
        u.setStoreId(rs.getLong("store_id"));
        return u;
    }
}
