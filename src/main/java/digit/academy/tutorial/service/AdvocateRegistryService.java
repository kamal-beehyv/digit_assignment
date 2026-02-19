package digit.academy.tutorial.service;

import digit.academy.tutorial.config.Configuration;
import digit.academy.tutorial.enrichment.AdvocateEnrichment;
import digit.academy.tutorial.kafka.Producer;
import digit.academy.tutorial.repository.AdvocateRepository;
import digit.academy.tutorial.validators.AdvocateValidator;
import digit.academy.tutorial.web.models.*;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Service
public class AdvocateRegistryService {

    @Autowired
    private AdvocateValidator validator;

    @Autowired
    private AdvocateEnrichment enrichment;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private AdvocateRepository repository;

    @Autowired
    private Producer producer;

    @Value("${advocate.kafka.create.topic:save-advocate-application}")
    private String createTopic;

    @Value("${advocate.kafka.update.topic:update-advocate-application}")
    private String updateTopic;

    public List<Advocate> create(AdvocateRequest request) {
        validator.validateCreateRequest(request);
        enrichment.enrichCreate(request);
        workflowService.updateWorkflowStatus(request);
        producer.push(createTopic, request);
        return request.getAdvocates();
    }

    public List<Advocate> search(RequestInfo requestInfo, AdvocateSearchCriteria criteria) {
        List<Advocate> advocates = repository.getAdvocates(criteria);
        return CollectionUtils.isEmpty(advocates) ? List.of() : advocates;
    }

    public Advocate update(AdvocateRequest request) {
        Advocate advocate = request.getAdvocates().get(0);
        Advocate existing = validator.validateApplicationExistence(advocate);
        existing.setWorkflow(advocate.getWorkflow());
        existing.setBarRegistrationNumber(advocate.getBarRegistrationNumber());
        existing.setAdvocateType(advocate.getAdvocateType());
        existing.setOrganisationID(advocate.getOrganisationID());
        existing.setIndividualId(advocate.getIndividualId());
        existing.setIsActive(advocate.getIsActive());
        existing.setDocuments(advocate.getDocuments());
        request.setAdvocates(Collections.singletonList(existing));
        enrichment.enrichUpdate(request);
        workflowService.updateWorkflowStatus(request);
        producer.push(updateTopic, request);
        return request.getAdvocates().get(0);
    }
}
