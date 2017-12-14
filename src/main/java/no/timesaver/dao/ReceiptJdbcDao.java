package no.timesaver.dao;

import no.timesaver.domain.Receipt;
import no.timesaver.domain.ReceiptProduct;
import no.timesaver.domain.StoreSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Repository
public class ReceiptJdbcDao implements ReceiptDao {

    private final JdbcTemplate template;
    private final SimpleJdbcInsert receiptInsertActor;

    @Autowired
    public ReceiptJdbcDao(JdbcTemplate jdbcTemplate){
        this.template = jdbcTemplate;
        receiptInsertActor = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("receipts")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<Receipt> getReceiptById(Long receiptId) {
        String sql= "SELECT Id, Confirmation_Code, Order_Time, User_Id FROM receipts WHERE id = ?";

        try {
            Receipt receipt = template.queryForObject(sql, (rs, rowNum) -> mapReceipt(rs),receiptId);
            return Optional.of(receipt);
        }
        catch (IncorrectResultSizeDataAccessException ie){
            return Optional.empty();
        }
    }

    @Override
    public List<Receipt> getReceiptsForUser(Long userId,int count) {
        String sql ="SELECT Id,Confirmation_Code, Order_Time, User_Id FROM receipts WHERE User_Id = ? ORDER BY order_time DESC LIMIT ?";
        return template.query(sql, (rs, rowNum) -> mapReceipt(rs),userId,count);
    }

    @Override
    public boolean confirmationCodeExists(String code) {
        String sql = "SELECT count(id) as count from receipts where confirmation_code = ?";
        return template.queryForObject(sql,(rs, rowNum) -> rs.getLong("count"),code) > 0;
    }

    @Override
    public void saveProductsForReceipt(Receipt receiptObject) {
        List<ReceiptProduct> products = receiptObject.getProducts();
        Long receiptId = receiptObject.getId();
        products.forEach(rp -> {
            String sql = "INSERT INTO receipt_products (receipt_id, product_count, product_id, price) " +
                    "VALUES (?,?,?,?)";
            template.update(sql,receiptId,rp.getCount(),rp.getProduct().getId(),rp.getPrice());
        });
    }



    @Override
    public void createReceiptsForStores(Map<Long,StoreSettings> storeIdToSetting,Long receiptId) {
        StringJoiner joiner = new StringJoiner("),(","(",")");
        storeIdToSetting.forEach((storeId,settings) -> joiner.add(
                receiptId+","+storeId+"," + "null,"+settings.getManualTimeVerification()+",null")
        );

        String insertSql = "INSERT INTO store_receipts (receipt_id, store_id, delivered_time,require_time_notification,ready_at) VALUES "+ joiner.toString();
        template.update(insertSql);
    }

    @Override
    public List<Long> getStoreIdsForProducts(Long receiptId) {
        String sql ="SELECT DISTINCT p.store_id " +
                "FROM receipt_products rp, product p, receipts r " +
                "WHERE rp.product_id = p.id and r.id = rp.receipt_id AND r.id = ?";
        return template.query(sql, (rs, rowNum) -> rs.getLong("store_id"),receiptId);
    }

    @Override
    public Long saveNewReceipt(Receipt receiptObject) {
        Map<String,Object> insertParams = new HashMap<>();
        insertParams.put("confirmation_code",receiptObject.getConfirmationCode());
        insertParams.put("order_time", Timestamp.valueOf(receiptObject.getOrderTime()));
        insertParams.put("deleted",false);
        insertParams.put("user_id",receiptObject.getUserId());

        return receiptInsertActor.executeAndReturnKey(insertParams).longValue();
    }

    @Override
    public List<Receipt> getOrdersByStoreById(Long storeId, boolean onlyActiveOrders) {
        String sql ="SELECT DISTINCT r.id, r.confirmation_code, r.order_time, r.user_Id, sr.delivered_time " +
                "FROM receipt_products rp, product p, receipts r, store_receipts sr " +
                "WHERE rp.product_id = p.id and p.store_id = ? and r.deleted is FALSE and r.id = rp.receipt_id and " +
                "sr.receipt_id = r.id and sr.store_id = ? and sr.delivered_time is " + (onlyActiveOrders ? "":"NOT") + " NULL;";

        return template.query(sql, new Object[]{storeId, storeId}, (rs, rowNum) -> mapReceiptsForStore(rs));
    }

    @Override
    public List<Receipt> getNewOrdersByStoreById(Long storeId, Long highestOrderId) {
        String sql ="SELECT DISTINCT r.id, r.confirmation_code, r.order_time, r.user_Id, sr.delivered_time " +
                "FROM receipt_products rp, product p, receipts r, store_receipts sr " +
                "WHERE rp.product_id = p.id and p.store_id = ? and r.deleted is FALSE and r.id = rp.receipt_id and " +
                "sr.receipt_id = r.id and sr.store_id = ? and sr.delivered_time is NULL AND r.id > ?";

        return template.query(sql, new Object[]{storeId, storeId, highestOrderId}, (rs, rowNum) -> mapReceiptsForStore(rs));
    }

    @Override
    public void setOrderToDelivered(Long orderIdToBeDelivered, Long storeId) {
        String sql ="UPDATE store_receipts SET delivered_time = ? where receipt_id = ? AND store_id = ?";
        template.update(sql, Timestamp.valueOf(LocalDateTime.now()), orderIdToBeDelivered, storeId);
    }

    @Override
    public void markOrderAsDeleted(Long orderId) {
        String sql ="UPDATE Receipts SET deleted = true where id = ?";
        template.update(sql, orderId);
    }

    @Override
    public void unDeliverOrderById(Long orderId, Long storeId) {
    String sql ="UPDATE store_receipts SET Delivered_Time = NULL WHERE receipt_id = ? and store_id = ?";
        template.update(sql,orderId, storeId);
    }

    @Override
    public void setReadyAtForStore(Long receiptId, Long storeId, LocalDateTime readyAt) {
        String sql = "Update store_receipts set ready_at=? where receipt_id =? and store_id=?";
        template.update(sql,Timestamp.valueOf(readyAt),receiptId,storeId);
    }

    @Override
    public boolean receiptsIncludeStore(Long receiptId, Long storeId) {
        String sql = "Select count(receipt_id) as count from store_receipts WHERE receipt_id =? and store_id =?";
        return 1 == template.queryForObject(sql,(rs, rowNum) -> rs.getLong("count"),receiptId,storeId);
    }

    private Receipt mapReceipt(ResultSet rs) throws SQLException {
        Receipt receipt = new Receipt(rs.getLong("Id"));
        receipt.setConfirmationCode(rs.getString("Confirmation_Code"));
        receipt.setOrderTime(LocalDateTime.ofInstant(rs.getTimestamp("Order_Time").toInstant(), ZoneId.systemDefault()));
        receipt.setUserId(rs.getLong("User_Id"));

        return populateDeliveryTime(receipt);
    }

    private Receipt populateDeliveryTime(Receipt receipt) {
        String sql = "Select * from store_receipts where receipt_id = ?";
        template.query(
                sql,
                (rs, rowNum) -> mapStoreSpecificProperties(rs, receipt)
                , receipt.getId()
        );

        return receipt;
    }

    private Receipt mapStoreSpecificProperties(ResultSet rs, Receipt receipt) throws SQLException {
        /*Map Delivery Time*/
        receipt.addToStoreIdToDeliveredTime(
                rs.getLong("Store_Id"),
                (rs.getTimestamp("Delivered_Time") == null) ? null : LocalDateTime.ofInstant(rs.getTimestamp("Delivered_Time").toInstant(), ZoneId.systemDefault())
        );

        /*Map require ready time verification*/
        receipt.addToStoredIdToRequiresReadyTimeNotification(rs.getLong("Store_Id"),rs.getBoolean("require_time_notification"));

        /*Map ready at*/
        receipt.addToStoredIdToReadyAt(
                rs.getLong("Store_Id"),
                (rs.getTimestamp("Ready_at") == null) ? null : LocalDateTime.ofInstant(rs.getTimestamp("Ready_at").toInstant(), ZoneId.systemDefault())
        );
        return receipt;
    }


    private Receipt mapReceiptsForStore(ResultSet rs) throws SQLException {
        Receipt receipt = new Receipt(rs.getLong("Id"));
        receipt.setConfirmationCode(rs.getString("Confirmation_Code"));
        if (rs.getTimestamp("Delivered_Time") == null){
            receipt.setDeliveredDate(null);
        } else {
            receipt.setDeliveredDate(LocalDateTime.ofInstant(rs.getTimestamp("Delivered_Time").toInstant(), ZoneId.systemDefault()));
        }
        receipt.setOrderTime(LocalDateTime.ofInstant(rs.getTimestamp("Order_Time").toInstant(), ZoneId.systemDefault()));
        receipt.setUserId(rs.getLong("User_Id"));

        return populateDeliveryTime(receipt);
    }
}
