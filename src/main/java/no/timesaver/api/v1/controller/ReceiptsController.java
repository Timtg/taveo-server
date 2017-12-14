package no.timesaver.api.v1.controller;

import no.timesaver.domain.Receipt;
import no.timesaver.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value ="/api/v1/receipts")
public class ReceiptsController {

    private final ReceiptService receiptService;

    @Autowired
    public ReceiptsController(ReceiptService receiptService){
        this.receiptService=receiptService;
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public @ResponseBody
    List<Receipt> getReceiptsForUser(@PathVariable Long userId,@RequestParam(required = false) Boolean all){
        if(all == null){
            all = false;
        }
        return receiptService.getReceiptsForUser(userId,all);
    }
}
