package no.timesaver.api.v1.controller;

import no.timesaver.domain.StoreSettings;
import no.timesaver.service.store.StoreSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value ="/api/v1/store/settings")
public class StoreSettingsController {

    private final StoreSettingsService storeSettingsService;

    @Autowired
    public StoreSettingsController(StoreSettingsService storeSettingsService) {
        this.storeSettingsService = storeSettingsService;
    }


    @RequestMapping(method = RequestMethod.GET,value = "/{storeId}")
    public @ResponseBody
    StoreSettings getSettingsForStore(@PathVariable Long storeId){
        return storeSettingsService.getSettingsForStore(storeId).orElse(null);
    }

    @RequestMapping(method = RequestMethod.PUT,value = "/{storeId}")
    public @ResponseBody
    void updateSettingsForStore(@PathVariable Long storeId,@RequestBody StoreSettings storeSettings){
        if(storeSettings.getStoreId() == null || storeSettings.getStoreId().equals(0L)){
            storeSettings.setStoreId(storeId);
        }
        if(!storeId.equals(storeSettings.getStoreId())){
            throw new IllegalArgumentException("The given storeId does not correspond with the storeId in the DTO");
        }
        storeSettingsService.updateSettingsForStore(storeSettings);
    }

}
