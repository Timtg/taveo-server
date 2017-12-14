package no.timesaver.dao;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserPasswordDao extends AbstractDao {

    Optional<String> getHashForUserByEmail(String email);

    void setResetInfo(Long id, String otpResetHash, LocalDateTime resetValidTo);

    void resetPassword(Long id, String hash);

    Optional<String> getResetCodeHashIfValid(Long userId);
}
