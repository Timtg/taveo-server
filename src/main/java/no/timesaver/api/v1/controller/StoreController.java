package no.timesaver.api.v1.controller;

import no.timesaver.api.v1.controller.linkers.ProductLinker;
import no.timesaver.api.v1.controller.linkers.StoreLinker;
import no.timesaver.api.v1.controller.linkers.StoreOpeningHoursLinker;
import no.timesaver.domain.Product;
import no.timesaver.domain.Receipt;
import no.timesaver.domain.Store;
import no.timesaver.domain.StoreOpeningHours;
import no.timesaver.exception.InternalServerException;
import no.timesaver.exception.NotYetImplementedException;
import no.timesaver.service.*;
import no.timesaver.service.store.StoreOpeningHoursService;
import no.timesaver.service.store.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value ="/api/v1/store")
public class StoreController {

    private final StoreService storeService;
    private final ProductFetchService productFetchService;
    private final StoreLinker storeLinker;
    private final ProductLinker productLinker;
    private final ReceiptService receiptService;
    private final StoreReceiptToPdfService receiptToPdfService;
    private final StoreOpeningHoursService storeOpeningHoursService;
    private final StoreOpeningHoursLinker storeOpeningHoursLinker;


    @Autowired
    public StoreController(StoreService storeService, ProductFetchService productFetchService, StoreLinker storeLinker, ProductLinker productLinker,
                           ReceiptService receiptService, StoreReceiptToPdfService receiptToPdfService,
                           StoreOpeningHoursService storeOpeningHoursService, StoreOpeningHoursLinker storeOpeningHoursLinker){
        this.storeService = storeService;
        this.productFetchService = productFetchService;
        this.storeLinker = storeLinker;
        this.productLinker = productLinker;
        this.receiptService = receiptService;
        this.receiptToPdfService = receiptToPdfService;
        this.storeOpeningHoursService = storeOpeningHoursService;
        this.storeOpeningHoursLinker = storeOpeningHoursLinker;
    }

    @RequestMapping(value = "/{storeId}", method = RequestMethod.GET)
    public @ResponseBody
    Resource<Store> getStoreById(@PathVariable Long storeId){
        return storeLinker.optionalToResource.apply(storeService.getStoreById(storeId));
    }

    @RequestMapping(value = "/{storeId}/opening-hours", method = RequestMethod.GET)
    public @ResponseBody
    Resource<StoreOpeningHours> getStoreOpeningHoursByStoreId(@PathVariable Long storeId){
        return storeOpeningHoursLinker.optionalToResource.apply(storeOpeningHoursService.getStoreOpeningHoursByStoreId(storeId));
    }

    @RequestMapping(value = "/opening-hours", method = RequestMethod.POST)
    public @ResponseBody
    void saveStoreOpeningHoursForStoreById(@RequestBody StoreOpeningHours body){
        if (!body.isComplete()) {
            throw new IllegalArgumentException("Unable to save the opening hours information as some information is missing");
        }

        storeOpeningHoursService.saveOpeningHoursForStore(body);
    }

    @RequestMapping(value = "/{storeId}", method = RequestMethod.PUT)
    public @ResponseBody
    Resource<Store> updateStoreById(@PathVariable Long storeId,@RequestBody Store update){
        return storeLinker.toResource.apply(storeService.updateStoreById(storeId,update));
    }

    @RequestMapping(value = "/{storeId}", method = RequestMethod.DELETE)
    public @ResponseBody
    Boolean deleteStoreById(@PathVariable Long storeId){
        if(true){
            throw new NotYetImplementedException("This feature is not yet implemented - please contact the developers");
        }
        return storeService.deleteStoreById(storeId);
    }

    @RequestMapping(value = "/{storeId}/products", method = RequestMethod.GET)
    public @ResponseBody
    List<Resource<Product>> getProductsByStoreId(@PathVariable Long storeId){
        return productLinker.listToResource.apply(productFetchService.getProductsForStoreById(storeId));
    }

    @RequestMapping(value = "/{storeId}/receipt/{receiptId}/pdf", method = RequestMethod.GET,produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody
    ResponseEntity<ByteArrayResource> getPdfForStoreReceipt(@PathVariable Long storeId, @PathVariable Long receiptId) throws IOException {
        Optional<String> storeNameForId = storeService.getStoreNameForId(storeId);
        if(!storeNameForId.isPresent()){
            throw new IllegalArgumentException("Unable to find store based on the given storeId");
        }

        Optional<Receipt> receiptById = receiptService.getReceiptById(receiptId,true);
        if(!receiptById.isPresent()){
            throw new IllegalArgumentException("Unable to find receipt based on the given receiptId");
        }

        Optional<byte[]> pdfBytes = receiptToPdfService.storeReceiptToPdf(receiptById.get(), storeId, storeNameForId.get());

        if(!pdfBytes.isPresent()){
            throw new InternalServerException("A general error occurred when generating the PDF for the receipt");
        }
        byte[] bytes = pdfBytes.get();
        ByteArrayResource bar = new ByteArrayResource(bytes);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_PDF);
        responseHeaders.setContentLength(bytes.length);
        String fileName = storeNameForId.get()+"_"+receiptById.get().getConfirmationCode()+"_"+receiptById.get().getOrderTime().format(DateTimeFormatter.ofPattern("dd/MM hh:mm"))+".pdf";
        responseHeaders.setContentDispositionFormData("attachment", fileName);
        return new ResponseEntity<>(bar, responseHeaders, HttpStatus.OK);
    }
}
