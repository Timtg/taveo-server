package no.timesaver.dao;

import no.timesaver.domain.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserDao extends AbstractDao {
    Optional<User> findById(Long userId);

    Optional<String> getHashForUserByEmail(String email);

    Optional<User> findByEmail(String email);

    boolean confirmDisclaimerForUserById(Long id);

    Optional<Long> getStoreIdForUser(Long userId);

    Optional<List<User>> getUsersForStore(Long storeId);

    boolean deleteUserById(Long userId);

    boolean updateStoreUser(User user);

    Optional<String> getUserNameById(Long userId);

    boolean userExists(Long userId);

    void setLastLogin(Long userId, LocalDateTime time);
}
