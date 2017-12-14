package no.timesaver.api.v1.controller;

import no.timesaver.api.v1.controller.linkers.AdLinker;
import no.timesaver.domain.Advertisement;
import no.timesaver.service.AdsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(value ="/api/v1/ad")
public class AdsController {

    private final AdsService adsService;

    @Autowired
    public AdsController(AdsService adsService) {
        this.adsService = adsService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody
    List<Resource<Advertisement>> getAllActiveAds(){
        return AdLinker.listToResource.apply(adsService.getAllActiveAds());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET,produces = APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody
    Resource<Advertisement> getAdById(@PathVariable Long id){
        return AdLinker.optionalToResource.apply(adsService.getAdById(id));
    }
}
