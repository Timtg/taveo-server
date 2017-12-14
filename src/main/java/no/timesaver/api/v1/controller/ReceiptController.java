package no.timesaver.api.v1.controller;

import no.timesaver.domain.Receipt;
import no.timesaver.exception.NotYetImplementedException;
import no.timesaver.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value ="/api/v1/receipt")
public class ReceiptController {

    private final ReceiptService receiptService;

    @Autowired
    public ReceiptController(ReceiptService receiptService){
        this.receiptService=receiptService;
    }

    @RequestMapping(value = "/{receiptId}", method = RequestMethod.GET)
    public @ResponseBody
    Optional<Receipt> getReceiptById(@PathVariable Long receiptId){
        return receiptService.getReceiptById(receiptId, false);
    }

    @RequestMapping(value = "/{orderId}/{storeId}", method = RequestMethod.POST)
    public @ResponseBody
    void markOrderToDelivered(@PathVariable Long orderId, @PathVariable Long storeId){
        receiptService.setOrderToDelivered(orderId, storeId);
    }

    @RequestMapping(value = "/delete/{orderId}", method = RequestMethod.POST)
    public @ResponseBody
    void markOrderAsDeleted(@PathVariable Long orderId){
        if(true){
            /*Temporarily disabled - need to think this through if an order contains products from multiple stores*/
            throw new NotYetImplementedException("This feature is not yet implemented - please contact the developers");
        }
        receiptService.markOrderAsDeleted(orderId);
    }


    @RequestMapping(value = "/orders/{storeId}", method = RequestMethod.GET)
    public @ResponseBody
    List<Receipt> getOrdersByStoreById(@PathVariable Long storeId, @RequestParam(value = "activeOrders") boolean onlyActiveOrders){
        return receiptService.getOrdersByStoreById(storeId,onlyActiveOrders);
    }

    @RequestMapping(value = "/orders/{storeId}/{highestOrderId}", method = RequestMethod.GET)
    public @ResponseBody
    List<Receipt> getNewOrdersByStoreById(@PathVariable Long storeId, @PathVariable Long highestOrderId){
        return receiptService.getNewOrdersByStoreById(storeId,highestOrderId);
    }

    @RequestMapping(value = "/unDeliver/{orderId}/{storeId}", method = RequestMethod.PUT)
    public @ResponseBody
    void unDeliverOrderById(@PathVariable Long orderId, @PathVariable Long storeId){
        receiptService.unDeliverOrderById(orderId, storeId);
    }

}
