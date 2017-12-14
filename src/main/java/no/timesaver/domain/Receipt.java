package no.timesaver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.timesaver.domain.types.UserTypeEnum;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Receipt implements UserProperty{

    private Long id;
    private String confirmationCode;
    private LocalDateTime orderTime;
    private LocalDateTime deliveredTime;
    private Map<Long,LocalDateTime> storedIdToDeliveredTime = new HashMap<>();
    private Map<Long,Boolean> storedIdToRequiresReadyTimeNotification = new HashMap<>();
    private Map<Long,LocalDateTime> storedIdToReadyAt = new HashMap<>();
    private Long userId;
    private List<ReceiptProduct> products;

    public Receipt() {
    }

    public Receipt(Long id) {
        this.id = id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setProducts(List<ReceiptProduct> products) {
        this.products = products;
    }

    public List<ReceiptProduct> getProducts() {
        return products;
    }

    public Map<Long, LocalDateTime> getStoredIdToDeliveredTime() {
        return storedIdToDeliveredTime;
    }

    public void addToStoreIdToDeliveredTime(Long storeId, LocalDateTime time){
        this.storedIdToDeliveredTime.put(storeId,time);
    }

    public Map<Long, Boolean> getStoredIdToRequiresReadyTimeNotification() {
        return storedIdToRequiresReadyTimeNotification;
    }

    public void addToStoredIdToRequiresReadyTimeNotification(Long storeId,Boolean readyTimeNotificationIsRequired) {
        this.storedIdToRequiresReadyTimeNotification.put(storeId,readyTimeNotificationIsRequired);
    }

    public Map<Long, LocalDateTime> getStoredIdToReadyAt() {
        return storedIdToReadyAt;
    }

    public void addToStoredIdToReadyAt(Long storeId,LocalDateTime time) {
        this.storedIdToReadyAt.put(storeId,time);
    }

    public String getDeliveryStatus() {
        if(storedIdToDeliveredTime.values().contains(null)){
            if(!storedIdToDeliveredTime.values().stream().filter(Objects::nonNull).collect(Collectors.toList()).isEmpty()) {
               return DeliveryStatus.PARTIAL.name();
            }
            return DeliveryStatus.UNDELIVERED.name();
        }
        return DeliveryStatus.COMPLETE.name();
    }

    public void setDeliveredDate(LocalDateTime deliveredDate) {
        this.deliveredTime = deliveredDate;
    }

    public LocalDateTime getDeliveredTime() {
        return deliveredTime;
    }

    @Override
    @JsonIgnore
    public long getAssociatedUserId() {
        return getUserId();
    }

    @Override
    @JsonIgnore
    public String getEntityType() {
        return "receipt";
    }

    @Override
    public boolean moderatorOrPickerHasAccess(User currentUser) {
        return UserTypeEnum.M.equals(currentUser.getType()) || UserTypeEnum.P.equals(currentUser.getType()) && this.allStoreIds().contains(currentUser.getStoreId());
    }

    public Optional<List<ReceiptProduct>> productsForStore(Long storeId){
        List<ReceiptProduct> collect = products.stream().filter(pr -> pr.getProduct().getStoreId().equals(storeId)).collect(Collectors.toList());
        if(collect.isEmpty()){
            return Optional.empty();
        }
        return Optional.of(collect);
    }

    public Optional<LocalDateTime> readyAtForStore(Long storeId){
        return Optional.ofNullable(storedIdToReadyAt.get(storeId));
    }

    public List<Long> allStoreIds() {
        return products.stream().map(p -> p.getProduct().getStoreId()).collect(Collectors.toSet()).stream().collect(Collectors.toList());
    }

    enum DeliveryStatus {
        COMPLETE,PARTIAL,UNDELIVERED
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Receipt receipt = (Receipt) o;

        if (id != null ? !id.equals(receipt.id) : receipt.id != null) return false;
        return confirmationCode != null ? confirmationCode.equals(receipt.confirmationCode) : receipt.confirmationCode == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (confirmationCode != null ? confirmationCode.hashCode() : 0);
        return result;
    }
}
