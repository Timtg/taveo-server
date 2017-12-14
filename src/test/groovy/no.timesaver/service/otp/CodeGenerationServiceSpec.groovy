package no.timesaver.service.otp

import spock.lang.Specification

class CodeGenerationServiceSpec extends Specification{

    CodeGenerationService service = new CodeGenerationService()

    def "When requesting a numeric otp of a given length x, the generated code should only contain numbers and always be of the correct length"(){
        List<String> codes = new ArrayList<>()
        def length = 10
        when: "generating some numeric otps"
        codes.add(service.generateNumericOTP(length))
        codes.add(service.generateNumericOTP(length))
        codes.add(service.generateNumericOTP(length))
        codes.add(service.generateNumericOTP(length))
        codes.add(service.generateNumericOTP(length))
        codes.add(service.generateNumericOTP(length))
        codes.add(service.generateNumericOTP(length))
        then: "all the generated codes should be of the given length and be numeric"
        assert codes.findAll{it.length() != length || !it.isNumber()}.isEmpty()
    }
}
