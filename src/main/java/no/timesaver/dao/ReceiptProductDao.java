package no.timesaver.dao;

import no.timesaver.domain.ReceiptProduct;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ReceiptProductDao extends AbstractDao {


    Map<Long, List<ReceiptProduct>> getProductsForReceiptIds(Set<Long> receiptIds, Long storeId);
}
