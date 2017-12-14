package no.timesaver.service

import no.timesaver.dao.ReceiptDao
import no.timesaver.domain.Product
import no.timesaver.domain.Receipt
import no.timesaver.domain.ReceiptProduct
import no.timesaver.domain.StoreSettings
import no.timesaver.domain.User
import no.timesaver.domain.types.UserTypeEnum
import no.timesaver.exception.MissingPermissionsException
import no.timesaver.security.AccessValidationService
import no.timesaver.service.otp.CodeGenerationService
import no.timesaver.service.store.StoreSettingsService
import no.timesaver.service.user.CurrentUserService
import no.timesaver.service.user.EmailVerificationService
import spock.lang.Specification

import java.time.LocalDateTime

class ReceiptServiceSpec extends Specification{
    ReceiptDao receiptDao = Mock(ReceiptDao)
    ReceiptProductService receiptProductService = Mock(ReceiptProductService)
    CodeGenerationService codeGenerationService = Mock(CodeGenerationService)
    AccessValidationService accessValidationService = Mock(AccessValidationService)
    CurrentUserService currentUserService = Mock(CurrentUserService)
    EmailVerificationService emailVerificationService = Mock(EmailVerificationService)
    StoreSettingsService storeSettingsService = Mock(StoreSettingsService)

    ReceiptService service

    def setup(){
        service = new ReceiptService(receiptDao,receiptProductService,codeGenerationService,accessValidationService,currentUserService,emailVerificationService,storeSettingsService)
    }

    def "When fetching a receipt, the access right for the user to the receipt should be checked"() {
        given: "receipt to be fetched"
        def receipt = new Receipt()
        receipt.id = 1337L

        when:"attempting to fetch a receipt"
        service.getReceiptById(receipt.id, false)
        then: "A call to chechk the access rights should be performed"
        1 * receiptDao.getReceiptById(receipt.id) >> Optional.of(receipt)
        1 * accessValidationService.validateAccess(receipt)
    }

    def "When placing an order, there must be a current user available"() {
        given: "an order to be placed and no currentUser available"
        List<ReceiptProduct> order = new ArrayList<>()
        currentUserService.getCurrentUser() >> Optional.empty()
        when: "attempting to save a new order and create receipts"
        service.saveNewOrderAndGetReceipt(order)
        then: "An IllegalStateException should be thrown as there isn't a currentUser available which is required"
        thrown IllegalStateException
    }

    def "When placing an order, the user must have an account type which is allowed to create an order - not moderator"() {
        given: "an order to be placed and a currentUser with type 'M(Moderator)'"
        User moderator = new User()
        moderator.setType(UserTypeEnum.M)
        moderator.setEmailVerified(true)
        moderator.setMobileVerified(true)


        List<ReceiptProduct> order = new ArrayList<>()
        currentUserService.getCurrentUser() >> Optional.of(moderator)
        when: "attempting to save a new order and create receipts as moderator"
        service.saveNewOrderAndGetReceipt(order)
        then: "An MissingPermissionsException should be thrown as the user isn't allowed to place an order"
        thrown MissingPermissionsException
    }

    def "When placing an order, the user must have an account type which is allowed to create an order - not picker"() {
        given: "an order to be placed and a currentUser with type 'P(Picker)'"
        User picker = new User()
        picker.setType(UserTypeEnum.P)
        picker.setEmailVerified(true)
        picker.setMobileVerified(true)


        List<ReceiptProduct> order = new ArrayList<>()
        currentUserService.getCurrentUser() >> Optional.of(picker)
        when: "attempting to save a new order and create receipts as Picker"
        service.saveNewOrderAndGetReceipt(order)
        then: "An MissingPermissionsException should be thrown as the user isn't allowed to place an order"
        thrown MissingPermissionsException
    }

    def "When saving an order the receipt, the receipt_products and the store_receipts should be saved with the correct parameters, and at the end a fresh instance of the receipt should be fetched"() {
        given: "an order to be placed"
        User normalUser = new User()
        normalUser.setType(UserTypeEnum.N)
        normalUser.setEmailVerified(true)
        normalUser.setMobileVerified(true)
        normalUser.id = 1337L

        def confirmationCode = "FOOBAR"

        def storeId1 = 1L
        def storeId2 = 2L
        HashMap<Long, StoreSettings> storeIdToStoreSettings = getStoreIdToStoreSettings(storeId1, storeId2)

        List<ReceiptProduct> order = getTestOrder(storeId1,storeId2)
        Set<Long> storeIdsForProductsInOrder = [storeId1,storeId2] as Set
        def receipt = getReceiptObject(normalUser.id, confirmationCode, order)
        def receiptId = 4567L
        def receiptWithId = getReceiptObject(normalUser.id, confirmationCode, order)
        receiptWithId.id = receiptId


        currentUserService.getCurrentUser() >> Optional.of(normalUser)
        def mockedReturnedReceiptId = 9876L
        when: "attempting to save a new order and create receipts as Picker"
        def returnedReceipt = service.saveNewOrderAndGetReceipt(order)
        then: "Then a series of calls should be made with the correct params"
        1 * receiptProductService.populatePriceForReceiptProducts(order) >> order
        1 * codeGenerationService.generateConfirmationCode() >> confirmationCode
        1 * receiptDao.confirmationCodeExists(confirmationCode) >> false
        1 * receiptDao.saveNewReceipt(receipt) >> receiptId
        1 * receiptDao.saveProductsForReceipt(receiptWithId)
        1 * storeSettingsService.getSettingsForStores(storeIdsForProductsInOrder) >> storeIdToStoreSettings
        1 * receiptDao.createReceiptsForStores(storeIdToStoreSettings,receiptId)
        1 * receiptDao.getReceiptById(receiptId) >> Optional.of(new Receipt(mockedReturnedReceiptId))
        assert returnedReceipt.id == mockedReturnedReceiptId
    }

    private HashMap<Long, StoreSettings> getStoreIdToStoreSettings(long storeId1, long storeId2) {
        StoreSettings ss1 = new StoreSettings()
        ss1.storeId = storeId1
        ss1.manualTimeVerification = true
        StoreSettings ss2 = new StoreSettings()
        ss2.storeId = storeId2
        ss2.manualTimeVerification = false
        Map<Long, StoreSettings> storeIdToStoreSettings = new HashMap<>()
        storeIdToStoreSettings.put(storeId1, ss1)
        storeIdToStoreSettings.put(storeId2, ss2)
        storeIdToStoreSettings
    }

    private ArrayList<ReceiptProduct> getTestOrder(storeId1,storeId2) {
        List<ReceiptProduct> order = new ArrayList<>()
        ReceiptProduct rp1 = new ReceiptProduct()
        Product p1 = new Product()
        p1.storeId = storeId1
        rp1.count = 1
        rp1.product = p1
        ReceiptProduct rp2 = new ReceiptProduct()
        Product p2 = new Product()
        p2.storeId = storeId2
        rp2.count = 1
        rp2.product = p2
        order.add(rp1)
        order.add(rp2)
        order
    }

    private Receipt getReceiptObject(Long userId, String confirmationCode,List<ReceiptProduct> products){
        Receipt r = new Receipt()
        r.setUserId(userId)
        r.setConfirmationCode(confirmationCode)
        r.setOrderTime(LocalDateTime.now())
        r.setProducts(products)
        return r
    }


}
