package no.timesaver.dao;

import no.timesaver.domain.Receipt;
import no.timesaver.domain.StoreSettings;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReceiptDao extends AbstractDao {

    Optional<Receipt> getReceiptById(Long receiptId);

    List<Receipt> getReceiptsForUser(Long userId,int count);

    boolean confirmationCodeExists(String code);

    void saveProductsForReceipt(Receipt receiptObject);

    void createReceiptsForStores(Map<Long, StoreSettings> storeIdToSetting, Long receiptId);

    List<Long> getStoreIdsForProducts(Long receiptId);

    Long saveNewReceipt(Receipt receiptObject);

    List<Receipt> getOrdersByStoreById(Long storeId, boolean onlyActiveOrders);

    List<Receipt> getNewOrdersByStoreById(Long storeId, Long highestOrderId);

    void setOrderToDelivered(Long orderToDelivered, Long storeId);

    void markOrderAsDeleted(Long orderId);

    void unDeliverOrderById(Long orderId, Long storeId);

    void setReadyAtForStore(Long receiptId, Long storeId, LocalDateTime readyAt);

    boolean receiptsIncludeStore(Long orderIdToBeDelivered, Long storeId);
}
