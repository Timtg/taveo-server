package no.timesaver.api.v1.controller;

import no.timesaver.domain.Receipt;
import no.timesaver.domain.ReceiptProduct;
import no.timesaver.service.ReceiptService;
import no.timesaver.service.store.StoreSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value ="/api/v1/order")
public class OrderController {


    private final ReceiptService receiptService;
    private final StoreSettingsService storeSettingsService;


    @Autowired
    public OrderController(ReceiptService receiptService, StoreSettingsService storeSettingsService){
        this.receiptService = receiptService;
        this.storeSettingsService = storeSettingsService;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody
    Resource<Receipt> placeOrder(@RequestBody List<ReceiptProduct> orderDto){
        if(orderDto == null || orderDto.isEmpty()){
            throw new IllegalStateException("Missing requestBody for order");
        }
        Receipt receipt = receiptService.saveNewOrderAndGetReceipt(orderDto);
        return new Resource<>(receipt);
    }

    @RequestMapping(value = "/time-verification", method = RequestMethod.POST)
    public @ResponseBody
    List<Long> getStoreIdsForWhichTimeVerificationIsRequired(@RequestParam List<Long> storeIds){
        if(storeIds == null || storeIds.isEmpty()){
            return Collections.emptyList();
        }

        return storeSettingsService.getStoreIdsForWhichTimeVerificationIsRequired(storeIds);
    }

    @RequestMapping(value = "/ready-at/{receiptId}/{storeId}", method = RequestMethod.PUT)
    public @ResponseBody
    void setOrderReadyAtForStore(@PathVariable Long receiptId, @PathVariable Long storeId, @RequestBody LocalDateTime readyAt){
        receiptService.setOrderReadyAtForStore(storeId,receiptId,readyAt);
    }


}
