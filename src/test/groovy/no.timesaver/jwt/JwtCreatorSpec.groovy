package no.timesaver.jwt

import no.timesaver.domain.types.UserTypeEnum
import spock.lang.Specification


class JwtCreatorSpec extends  Specification{

    JwtKeyProvider jwtKeyProvider = Mock(JwtKeyProvider)

    JwtCreator service;

    def setup(){
        service = new JwtCreator(jwtKeyProvider)
    }

    def "When the user is a normal user the token should have a month expiry"(){
        given: "A normal type"
        def type = UserTypeEnum.N
        when: "getting the expiry period"
        float expiryMin = service.getJwtExpirationMinutes(type)
        then: "The expiry should be one month in minutes"
        assert expiryMin == (60F * 24F * 7F *4F)
    }
    def "When the user is a moderator, the token should have a day expiry for security reasons"(){
        given: "A moderator type"
        def type = UserTypeEnum.M
        when: "getting the expiry period"
        float expiryMin = service.getJwtExpirationMinutes(type)
        then: "The expiry should be one day in minutes"
        assert expiryMin == (60F * 24F)
    }
    def "When the user is an admin, the token should have a day expiry for security reasons"(){
        given: "A moderator type"
        def type = UserTypeEnum.A
        when: "getting the expiry period"
        float expiryMin = service.getJwtExpirationMinutes(type)
        then: "The expiry should be one day in minutes"
        assert expiryMin == (60F * 24F)
    }

}
