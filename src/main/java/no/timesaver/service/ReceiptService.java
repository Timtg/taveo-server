package no.timesaver.service;

import no.timesaver.dao.ReceiptDao;
import no.timesaver.domain.*;
import no.timesaver.domain.types.UserTypeEnum;
import no.timesaver.exception.DataIntegrityException;
import no.timesaver.exception.MissingPermissionsException;
import no.timesaver.security.AccessValidationService;
import no.timesaver.service.otp.CodeGenerationService;
import no.timesaver.service.pushnotification.PushNotificationSendService;
import no.timesaver.service.store.StoreService;
import no.timesaver.service.store.StoreSettingsService;
import no.timesaver.service.user.CurrentUserService;
import no.timesaver.service.user.EmailVerificationService;
import no.timesaver.service.user.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class ReceiptService {

    private final ReceiptDao receiptDao;
    private final ReceiptProductService receiptProductService;
    private final CodeGenerationService codeGenerationService;
    private final AccessValidationService accessValidationService;
    private final CurrentUserService currentUserService;
    private final EmailVerificationService emailVerificationService;

    private final static Logger log = LoggerFactory.getLogger(ReceiptService.class);
    private final StoreSettingsService storeSettingsService;
    private final PushNotificationSendService pushNotificationSendService;
    private final StoreService storeService;
    private final UserInfoService userInfoService;

    private final static int RECEIPT_FETCH_COUNT = 10;

    @Autowired
    public ReceiptService(
            ReceiptDao receiptDao,
            ReceiptProductService receiptProductService,
            CodeGenerationService codeGenerationService,
            AccessValidationService accessValidationService,
            CurrentUserService currentUserService,
            EmailVerificationService emailVerificationService,
            StoreSettingsService storeSettingsService,
            PushNotificationSendService pushNotificationSendService,
            StoreService storeService,
            UserInfoService userInfoService
    ) {
        this.receiptDao = receiptDao;
        this.receiptProductService = receiptProductService;
        this.codeGenerationService = codeGenerationService;
        this.accessValidationService = accessValidationService;
        this.currentUserService = currentUserService;
        this.emailVerificationService = emailVerificationService;
        this.storeSettingsService = storeSettingsService;
        this.pushNotificationSendService = pushNotificationSendService;
        this.storeService = storeService;
        this.userInfoService = userInfoService;
    }

    public Optional<Receipt> getReceiptById(Long receiptId, boolean mapProducts) {
        Optional<Receipt> receipt = receiptDao.getReceiptById(receiptId);
        receipt.ifPresent(r -> {
            accessValidationService.validateAccess(r);
            if(mapProducts){
                Map<Long, List<ReceiptProduct>> productsForReceiptIds = receiptProductService.getProductsForReceiptIds(Stream.of(receiptId).collect(Collectors.toSet()));
                r.setProducts(productsForReceiptIds.get(receiptId));
            }
        });

        return receipt;
    }

    public List<Receipt> getReceiptsForUser(Long userId, Boolean all) {
        int count = all ? Integer.MAX_VALUE : RECEIPT_FETCH_COUNT;


        List<Receipt> receiptsForUser = receiptDao.getReceiptsForUser(userId,count);
        receiptsForUser.forEach(accessValidationService::validateAccess);
        Map<Long,List<ReceiptProduct>> receiptIdToProducts = receiptProductService.getProductsForReceiptIds(receiptsForUser.stream().map(Receipt::getId).collect(Collectors.toSet()));
        receiptsForUser.forEach(receipt -> receipt.setProducts(receiptIdToProducts.get(receipt.getId())));
        return receiptsForUser;
    }

    public void setOrderToDelivered(Long orderIdToBeDelivered, Long storeId) {
        if(!receiptDao.receiptsIncludeStore(orderIdToBeDelivered,storeId)){
            throw new IllegalArgumentException("The specified order does not contain products from the specified store");
        }

        User cu = currentUserService.getCurrentUser().orElseThrow(() -> new IllegalStateException("Missing current user for editing a product"));
        if(!accessValidationService.canGetOrModifyOrderForStore(cu, storeId)){
            throw new SecurityException("The current user is not allowed to set the delivery status for the specified store");
        }



        receiptDao.setOrderToDelivered(orderIdToBeDelivered, storeId);
        log.info("Marked order with id={} as delivered", orderIdToBeDelivered);
    }

    public void markOrderAsDeleted(Long orderId) {
        if(true){
            /*Temporarily disabled - need to think this through if an order contains products from multiple stores*/
            return;
        }

        receiptDao.markOrderAsDeleted(orderId);
        log.info("Order with Id={} marked as deleted by user with id={}", orderId, currentUserService.getCurrentUser().orElse(null).getId());
    }

    public List<Receipt> getOrdersByStoreById(Long storeId, boolean onlyActiveOrders) {
        User cu = currentUserService.getCurrentUser().orElseThrow(() -> new IllegalStateException("Missing current user for editing a product"));
        if(!accessValidationService.canGetOrModifyOrderForStore(cu, storeId)){
            throw new SecurityException("The current user is not allowed to get orders for the given store id, as it is not associated with the specific store");
        }

        List<Receipt> receiptsForStore =  receiptDao.getOrdersByStoreById(storeId, onlyActiveOrders);
        return getReceipts(storeId, receiptsForStore);
    }

    public List<Receipt> getNewOrdersByStoreById(Long storeId, Long highestOrderId) {
        User cu = currentUserService.getCurrentUser().orElseThrow(() -> new IllegalStateException("Missing current user for editing a product"));
        if(!accessValidationService.canGetOrModifyOrderForStore(cu, storeId)){
            throw new SecurityException("The current user is not allowed to get orders for the given store id, as it is not associated with the specific store");
        }

        List<Receipt> receiptsForStore =  receiptDao.getNewOrdersByStoreById(storeId, highestOrderId);
        return getReceipts(storeId, receiptsForStore);
    }

    private List<Receipt> getReceipts(Long storeId, List<Receipt> receiptsForStore) {
        Map<Long,List<ReceiptProduct>> receiptIdToProducts = receiptProductService.getProductsForReceiptIdsByStoreId(receiptsForStore.stream().map(Receipt::getId).collect(Collectors.toSet()), storeId);
        receiptsForStore.forEach(receipt -> receipt.setProducts(receiptIdToProducts.get(receipt.getId())));
        return receiptsForStore;
    }

    public Receipt saveNewOrderAndGetReceipt(List<ReceiptProduct> order) {
        /*Ensure user is allowed to place an order*/
        Optional<User> currentUser = currentUserService.getCurrentUser();
        User userfromToken = currentUser.orElseThrow(() -> new IllegalStateException("Missing current user for restricted endpoint"));
        User user = userInfoService.getById(userfromToken.getId());
        canUserPlaceOrder(user,order);

        /*Populate missing information for the order and save the main receipt*/
        order = receiptProductService.populatePriceForReceiptProducts(order);
        Receipt receiptObject = getReceiptObject(user.getId(), getNewUniqueConfirmationCode(), order);
        Long receiptId = receiptDao.saveNewReceipt(receiptObject);
        receiptObject.setId(receiptId);

        /*Save all the products in the receipts*/
        receiptDao.saveProductsForReceipt(receiptObject);

        /*save store_receipts (order breakdown per store*/
        Set<Long> storeIds = order.stream().map(ReceiptProduct::getProduct).map(Product::getStoreId).collect(Collectors.toSet());
        Map<Long,StoreSettings> storeIdToSetting = storeSettingsService.getSettingsForStores(storeIds);
        receiptDao.createReceiptsForStores(storeIdToSetting,receiptId);

        /*Fetch the receipts once more from the dao to ensure all mappings are populated correctly in the receipt object*/
        return receiptDao.getReceiptById(receiptId).orElseThrow(() -> new DataIntegrityException("Unable to find receipt for new order"));
    }

    public void unDeliverOrderById(Long orderId, Long storeId) {
        User cu = currentUserService.getCurrentUser().orElseThrow(() -> new IllegalStateException("Missing current user for editing a product"));
        if(!accessValidationService.canGetOrModifyOrderForStore(cu, storeId)){
            throw new SecurityException("The current user is not allowed to undeliver the order for the given store id, as it is not associated with the specific store");
        }

        receiptDao.unDeliverOrderById(orderId, storeId);
        log.info("Order with id={} marked as undelivered", orderId);
    }

    private void canUserPlaceOrder(User user, List<ReceiptProduct> order) {
        if(!user.isEmailVerified()){
            emailVerificationService.generateAndSendEmailVerificationCode(user.getEmail(),user.getId(),user.getName());
            throw new IllegalStateException("The user account has not yet verified their email address, and can therefor not yet place any order. A new verification email has been sent to " +user.getEmail());
        }
        if(!user.isMobileVerified()){
            throw new IllegalStateException("The user account has not yet verified their mobile number, and can therefor not yet place an order.");
        }
        Set<UserTypeEnum> nonApplicableUserTypes = Stream.of(UserTypeEnum.P,UserTypeEnum.M).collect(Collectors.toSet());
        if(nonApplicableUserTypes.contains(user.getType())){
            throw new MissingPermissionsException("A user of type " + user.getType().toString() + " is not allowed to place order. Create a normal account to place an order");
        }

        if(user.getStoreId() != null && !user.getStoreId().equals(0L)){
            Set<Long> storeIdsInOrder = order.stream().map(rp -> rp.getProduct().getStoreId()).collect(Collectors.toSet());
            if(storeIdsInOrder.size() > 1 || !storeIdsInOrder.contains(user.getStoreId())){
                throw new MissingPermissionsException("The user is a single-store user, but attempted to place an order with products from other stores than the associated one");
            }
        }
    }

    private String getNewUniqueConfirmationCode() {
        boolean exists = true;
        String code = null;
        while (exists) {
            code = codeGenerationService.generateConfirmationCode();
            exists = receiptDao.confirmationCodeExists(code);
        }
        return code;
    }

    private Receipt getReceiptObject(Long userId, String confirmationCode,List<ReceiptProduct> products){
        Receipt r = new Receipt();
        r.setUserId(userId);
        r.setConfirmationCode(confirmationCode);
        r.setOrderTime(LocalDateTime.now());
        r.setProducts(products);
        return r;
    }

    public Boolean setOrderReadyAtForStore(Long storeId, Long receiptId, LocalDateTime readyAt) {
        //check that the current user is moderator or picker (or admin) for the storeId
        User cu = currentUserService.getCurrentUser().orElseThrow(() -> new IllegalStateException("Missing current user for setting ready-at time for orders"));
        if(UserTypeEnum.N.equals(cu.getType())){
            throw new MissingPermissionsException("Normal users are not allowed to set a ready-at time for orders");
        }
        if(cu.getStoreId() == null || !cu.getStoreId().equals(storeId)){
            throw new MissingPermissionsException("The current user is not allowed to set the ready-at time for the given store");
        }

        Receipt receipt = getReceiptById(receiptId, false).map(r -> r).orElseThrow(() -> new IllegalArgumentException("No receipts found for the receiptId given: " + receiptId));
        String storeName = storeService.getStoreNameForId(storeId).map(s -> s).orElseThrow(() -> new IllegalArgumentException("No store found for the receiptId given: " + storeId));

        //check that the store's store_settings is set to require manual_time_verification
        Optional<StoreSettings> settingsForStore = storeSettingsService.getSettingsForStore(storeId);
        if(!settingsForStore.isPresent() || !settingsForStore.get().getManualTimeVerification()){
            throw new IllegalStateException("The given store does not require a ready-at time to be set for their orders");
        }

        //Update store_receipts with readyAtTime
        receiptDao.setReadyAtForStore(receiptId,storeId,readyAt);


        String title = "Bestilling " + receipt.getConfirmationCode() + " oppdatert";
        String message = "Din bestilling " + receipt.getConfirmationCode() + " ved " + storeName +" har blitt oppdatert og kan hentes " + readyAt.format(DateTimeFormatter.ofPattern("dd/MM-yyyy hh:mm"));
        return pushNotificationSendService.sendNotificationToUser(receipt.getUserId(),title,message,receipt.getId());
    }
}
