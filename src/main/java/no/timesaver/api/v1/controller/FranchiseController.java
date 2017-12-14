package no.timesaver.api.v1.controller;

import no.timesaver.api.v1.controller.linkers.FranchiseLinker;
import no.timesaver.api.v1.controller.linkers.StoreLinker;
import no.timesaver.domain.Franchise;
import no.timesaver.domain.Store;
import no.timesaver.service.FranchiseService;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value ="/api/v1/franchise")
public class FranchiseController {

    private final FranchiseService franchiseService;
    private final StoreLinker storeLinker;
    private final FranchiseLinker franchiseLinker;

    public FranchiseController(FranchiseService franchiseService, StoreLinker storeLinker, FranchiseLinker franchiseLinker) {
        this.franchiseService = franchiseService;
        this.storeLinker = storeLinker;
        this.franchiseLinker = franchiseLinker;
    }

    @RequestMapping(value = "/{franchiseId}", method = RequestMethod.GET)
    public @ResponseBody
    Resource<Franchise> getFranchiseById(@PathVariable Long franchiseId) {
        return franchiseLinker.optionalToResource.apply(franchiseService.getById(franchiseId));
    }

    @RequestMapping(value = "/{franchiseId}/stores", method = RequestMethod.GET)
    public @ResponseBody
    List<Resource<Store>> getStoresByFranchiseId(@PathVariable Long franchiseId){
        return storeLinker.listToResource.apply(franchiseService.getStoresByFranchiseId(franchiseId));
    }
}
