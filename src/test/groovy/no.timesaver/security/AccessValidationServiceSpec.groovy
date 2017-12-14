package no.timesaver.security

import no.timesaver.domain.Receipt
import no.timesaver.domain.User
import no.timesaver.domain.types.UserTypeEnum
import no.timesaver.service.user.CurrentUserService
import spock.lang.Specification

class AccessValidationServiceSpec  extends Specification{
    CurrentUserService currentUserService = Mock(CurrentUserService)

    AccessValidationService service

    def setup() {
        service = new AccessValidationService(currentUserService)
    }

    def "A normal (non-admin) user should not be allowed to fetch objects defined as userProperty"() {
        given: "a non-user admin"
        User u = new User()
        u.setId(123L)
        u.setType(UserTypeEnum.N)
        currentUserService.getCurrentUser() >> Optional.of(u)


        Receipt userProperty = new Receipt()
        userProperty.userId = 321L
        when:"attempting to fetch a receipt for another user"
        service.validateAccess(userProperty)
        then: "a SecurityException should be thrown after the access right has been checked as the current user does not match the associated userId"
        thrown SecurityException
    }

    def "A moderator (non-admin) user should not be allowed to fetch objects defined as userProperty"() {
        given: "a non-user admin"
        User u = new User()
        u.setId(123L)
        u.setType(UserTypeEnum.M)
        currentUserService.getCurrentUser() >> Optional.of(u)


        Receipt userProperty = new Receipt()
        userProperty.userId = 321L
        when:"attempting to fetch a receipt for another user"
        service.validateAccess(userProperty)
        then: "a SecurityException should be thrown after the access right has been checked as the current user does not match the associated userId"
        thrown SecurityException
    }

    def "A admin user should be allowed to fetch objects defined as userProperty"() {
        given: "a user admin"
        User u = new User()
        u.setId(123L)
        u.setType(UserTypeEnum.A)
        currentUserService.getCurrentUser() >> Optional.of(u)


        Receipt userProperty = new Receipt()
        userProperty.userId = 321L
        when:"attempting to fetch a receipt for another user"
        def ret = service.validateAccess(userProperty)
        then: "access should be granted without any exceptions beeing thrown"
        assert ret

    }
}
