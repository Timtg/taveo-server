package no.timesaver.api.v1.controller;

import no.timesaver.api.v1.controller.linkers.StoreLinker;
import no.timesaver.domain.Store;
import no.timesaver.service.store.StoreService;
import no.timesaver.service.store.StoreSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value ="/api/v1/stores")
public class StoresController {

    private final StoreService storeService;
    private final StoreLinker storeLinker;
    private final StoreSettingsService storeSettingsService;


    @Autowired
    public StoresController(StoreService storeService, StoreLinker storeLinker, StoreSettingsService storeSettingsService){
        this.storeService = storeService;
        this.storeLinker = storeLinker;
        this.storeSettingsService = storeSettingsService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    List<Resource<Store>> getAllStores(){
        List<Store> stores = storeService.getAllActiveStores();
        return stores.stream().map(store -> storeLinker.toResource.apply(store)).collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/time-verification")
    public @ResponseBody
    Set<String> getStoresWhichRequireTimeVerificationByIds(@RequestParam List<Long> ids){
        List<Long> eligibleStoreIds = storeSettingsService.getStoreIdsForWhichTimeVerificationIsRequired(ids);
        return storeService.getStoreNameForIds(eligibleStoreIds);
    }
}
