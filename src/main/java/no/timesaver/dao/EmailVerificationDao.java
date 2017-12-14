package no.timesaver.dao;

import java.util.Optional;

public interface EmailVerificationDao extends AbstractDao{

   Optional<String> getVerificationHashForUser(long userId);
   boolean removeVerificationEntryByUserId(long userId);

   void setEmailVerifiedForUser(Long userId);

   void addVerificationEntryForUserId(long userId, String otp);
}
