package no.timesaver.service;

import no.timesaver.dao.ReceiptProductDao;
import no.timesaver.domain.ReceiptProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReceiptProductService {


    private final ReceiptProductDao receiptProductDao;
    private final ProductPriceService productPriceService;

    @Autowired
    public ReceiptProductService(ReceiptProductDao receiptProductDao,ProductPriceService productPriceService) {
        this.receiptProductDao = receiptProductDao;
        this.productPriceService = productPriceService;
    }


    Map<Long, List<ReceiptProduct>> getProductsForReceiptIds(Set<Long> receiptIds) {
        return receiptProductDao.getProductsForReceiptIds(receiptIds,null);
    }

    Map<Long, List<ReceiptProduct>> getProductsForReceiptIdsByStoreId(Set<Long> receiptIds, Long storeId) {
        return receiptProductDao.getProductsForReceiptIds(receiptIds,storeId);
    }

    List<ReceiptProduct> populatePriceForReceiptProducts(List<ReceiptProduct> receiptProducts){
        Set<Long> productIds = receiptProducts.stream().map(rp -> rp.getProduct().getId()).collect(Collectors.toSet());
        Map<Long,BigDecimal> productIdToPrice = productPriceService.getPriceForProductsByIds(productIds);
        receiptProducts.forEach(rp -> rp.setPrice(productIdToPrice.get(rp.getProduct().getId())));
        return receiptProducts;
    }
}
