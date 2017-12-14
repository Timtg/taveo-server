package no.timesaver.service.user

import no.timesaver.dao.UserPasswordDao
import no.timesaver.service.mail.MailSenderService
import no.timesaver.service.otp.CodeGenerationService
import spock.lang.Specification


class PasswordServiceSpec extends Specification{

    UserPasswordDao userPasswordDao = Mock(UserPasswordDao)
    CodeGenerationService codeGenerationService = Mock(CodeGenerationService)
    MailSenderService mailSenderService = Mock(MailSenderService)

    PasswordService service

    def setup() {
        service = new PasswordService(userPasswordDao,codeGenerationService,mailSenderService)
    }

    def "When validating a valid password - hash combination, should return true"() {
        given: "a pw and it's corresponding hash"
        def pw = "foo-bar"
        def hash = "\$2a\$04\$N5.ZW5sMAmUp9McIEHZwCOAw7lj3S2KMNM9sPZbj6P6in3n38eh7m"
        when: "validating the combination"
        def isValid = service.validate(pw,hash)
        then: "it should be evaluated to valid"
        assert isValid
    }

    def "When validating an invalid password - hash combination, should return false"() {
        given: "a pw and it's (stated)corresponding hash"
        def pw = "foo-baz"
        def hash = "\$2a\$04\$N5.ZW5sMAmUp9McIEHZwCOAw7lj3S2KMNM9sPZbj6P6in3n38eh7m"
        when: "validating the combination"
        def isValid = service.validate(pw,hash)
        then: "it should be evaluated to not valid"
        assert !isValid
    }


}
