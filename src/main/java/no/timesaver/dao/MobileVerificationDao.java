package no.timesaver.dao;

import java.util.Optional;

public interface MobileVerificationDao extends AbstractDao{

   Optional<String> getVerificationHashForUser(long userId);
   boolean removeVerificationEntryByUserId(long userId);

   void setMobileVerifiedForUser(Long userId);

   void addVerificationEntryForUserId(long userId, String otp);
}
