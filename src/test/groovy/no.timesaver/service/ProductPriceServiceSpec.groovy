package no.timesaver.service

import no.timesaver.dao.ProductDao
import no.timesaver.tools.ObjectValidator
import org.apache.commons.lang3.tuple.Pair
import spock.lang.Specification

import java.time.LocalDateTime

class ProductPriceServiceSpec extends Specification{

    ObjectValidator objectValidator = Mock(ObjectValidator)
    ProductDao productDao = Mock(ProductDao)

    ProductPriceService service

    def setup() {
        service = new ProductPriceService(objectValidator,productDao)
    }

    def "changePrice should validate the inputs" () {
        given: "some empty input params"
        def productId = 0L
        BigDecimal newPrice = BigDecimal.ONE;
        def validTo = LocalDateTime.now();
        def emptyObject = null
        when: "attempting to change the price"
        service.changePrice(productId,newPrice,validTo,emptyObject)
        then: "an IllegalArgumentException is thrown indicating bad input params"
        1 * objectValidator.isValid(_)
        thrown IllegalArgumentException
    }

    def "setting offer should not be possible is there already is an existing offer for a product"(){
        given: "overlapping time periods"
        LocalDateTime validTo = LocalDateTime.now().plusDays(11)
        when: "checking if time periods overlap"
        productDao.getOfferForProduct(_) >> Optional.of(Pair.of(LocalDateTime.now(), LocalDateTime.now().plusDays(10)))
        def isOverlapping = service.overlapsWithExistingOffer(-1,validTo)
        then: "time periods should overlap"
        assert isOverlapping
    }

    def "setting offer should be possible if there are no overlapping offer periods for a product"(){
        given: "overlapping time periods"
        LocalDateTime validTo = LocalDateTime.now().plusDays(2)
        when: "checking if time periods overlap"
        productDao.getOfferForProduct(_) >> Optional.of(Pair.of(LocalDateTime.now().minusDays(4), LocalDateTime.now().minusDays(2)))
        def isOverlapping = service.overlapsWithExistingOffer(-1,validTo)
        then: "time periods should not overlap"
        assert !isOverlapping
    }

}