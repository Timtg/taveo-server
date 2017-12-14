package no.timesaver.api.v1.controller;

import no.timesaver.api.v1.dto.NewInitialClientDto;
import no.timesaver.security.AccessValidationService;
import no.timesaver.service.admin.NewClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value ="/api/v1/admin/new-client")
public class AdminNewClientController {

    private final AccessValidationService accessValidationService;
    private final NewClientService newClientService;

    @Autowired
    public AdminNewClientController(AccessValidationService accessValidationService, NewClientService newClientService) {
        this.accessValidationService = accessValidationService;
        this.newClientService = newClientService;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody
    Resource<Boolean> addNewClient(@RequestBody NewInitialClientDto newInitialClientDto){
        accessValidationService.adminRequiredValidation();
        newClientService.add(newInitialClientDto);
        return new Resource<>(true);
    }
}
