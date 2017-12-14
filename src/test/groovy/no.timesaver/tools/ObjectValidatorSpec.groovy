package no.timesaver.tools

import spock.lang.Specification

import java.time.LocalDateTime

class ObjectValidatorSpec extends Specification{

    ObjectValidator service;

    def setup() {
        service = new ObjectValidator()
    }

    def "emty objects should be evaluated to invalid" () {
        given: "some empty input params"
        def productId = 0L
        BigDecimal newPrice = BigDecimal.ONE;
        def validTo = LocalDateTime.now();
        def emptyObject = null;
        when: "attempting to change the price"
        def valid = service.isValid(Arrays.asList(productId, newPrice, validTo, emptyObject))
        then: "an IllegalArgumentException is thrown indicating bad input params"
        assert !valid
    }


}