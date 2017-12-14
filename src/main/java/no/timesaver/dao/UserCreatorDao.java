package no.timesaver.dao;

import no.timesaver.domain.User;

public interface UserCreatorDao extends AbstractDao {
    boolean canBeCreated(String email, String mobile);

    Number createNew(User userInfo);

}
