package no.timesaver.domain;


import java.math.BigDecimal;

public class ReceiptProduct {
    private BigDecimal price;
    private Long count;
    private Product product;
    private long receiptId;

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setReceiptId(long receiptId) {
        this.receiptId = receiptId;
    }

    public long getReceiptId() {
        return receiptId;
    }
}
