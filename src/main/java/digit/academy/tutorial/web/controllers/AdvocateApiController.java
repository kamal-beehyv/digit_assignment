package digit.academy.tutorial.web.controllers;

import digit.academy.tutorial.service.AdvocateRegistryService;
import digit.academy.tutorial.util.ResponseInfoFactory;
import digit.academy.tutorial.web.models.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping("")
@Tag(name = "Advocate", description = "Advocate Registry API – create, update, search")
public class AdvocateApiController {

    private final AdvocateRegistryService advocateRegistryService;
    private final ResponseInfoFactory responseInfoFactory;

    public AdvocateApiController(AdvocateRegistryService advocateRegistryService,
                                 ResponseInfoFactory responseInfoFactory) {
        this.advocateRegistryService = advocateRegistryService;
        this.responseInfoFactory = responseInfoFactory;
    }

    @Operation(summary = "Register a new advocate")
    @RequestMapping(value = "/v1/_create", method = RequestMethod.POST)
    public ResponseEntity<AdvocateResponse> create(@Valid @RequestBody AdvocateRequest request) {
        List<Advocate> advocates = advocateRegistryService.create(request);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
        return ResponseEntity.status(HttpStatus.OK).body(AdvocateResponse.builder()
                .responseInfo(responseInfo)
                .advocates(advocates)
                .build());
    }

    @Operation(summary = "Update advocate")
    @RequestMapping(value = "/v1/_update", method = RequestMethod.POST)
    public ResponseEntity<AdvocateResponse> update(@Valid @RequestBody AdvocateRequest request) {
        Advocate advocate = advocateRegistryService.update(request);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
        return ResponseEntity.status(HttpStatus.OK).body(AdvocateResponse.builder()
                .responseInfo(responseInfo)
                .advocates(List.of(advocate))
                .build());
    }

    @Operation(summary = "Search advocates")
    @RequestMapping(value = "/v1/_search", method = RequestMethod.POST)
    public ResponseEntity<AdvocateResponse> search(@Valid @RequestBody AdvocateSearchRequest request) {
        AdvocateSearchCriteria criteria = request.getCriteria() != null ? request.getCriteria() : AdvocateSearchCriteria.builder().build();
        List<Advocate> advocates = advocateRegistryService.search(request.getRequestInfo(), criteria);
        ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
        return ResponseEntity.status(HttpStatus.OK).body(AdvocateResponse.builder()
                .responseInfo(responseInfo)
                .advocates(advocates)
                .build());
    }
}
