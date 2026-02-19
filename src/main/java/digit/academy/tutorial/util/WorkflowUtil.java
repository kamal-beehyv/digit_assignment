package digit.academy.tutorial.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import digit.academy.tutorial.config.Configuration;
import digit.academy.tutorial.repository.ServiceRequestRepository;
import digit.academy.tutorial.web.models.RequestInfoWrapper;
import digit.academy.tutorial.web.models.Workflow;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.common.contract.workflow.*;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static digit.academy.tutorial.config.ServiceConstants.*;

@Service
public class WorkflowUtil {

    @Autowired
    private ServiceRequestRepository repository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private Configuration configs;

    @Autowired
    private UserUtil userUtil;

    public BusinessService getBusinessService(RequestInfo requestInfo, String tenantId, String businessServiceCode) {
        StringBuilder url = new StringBuilder(configs.getWfHost())
                .append(configs.getWfBusinessServiceSearchPath())
                .append(TENANTID).append(tenantId)
                .append(BUSINESS_SERVICES).append(businessServiceCode);
        RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
        Object result = repository.fetchResult(url, requestInfoWrapper);
        BusinessServiceResponse response = mapper.convertValue(result, BusinessServiceResponse.class);
        if (CollectionUtils.isEmpty(response.getBusinessServices())) {
            throw new CustomException(BUSINESS_SERVICE_NOT_FOUND, THE_BUSINESS_SERVICE + businessServiceCode + NOT_FOUND);
        }
        return response.getBusinessServices().get(0);
    }

    public String updateWorkflowStatus(RequestInfo requestInfo, String tenantId, String businessId,
                                        String businessServiceCode, Workflow workflow,
                                        String wfModuleName) {
        // Enrich RequestInfo with user roles when client sends only uuid (no roles).
        // Workflow validates action against user roles (e.g. VERIFY requires ADVOCATE_VERIFIER).
        userUtil.enrichUserInfoWithRoles(requestInfo, tenantId);
        ProcessInstance processInstance = getProcessInstanceForWorkflow(requestInfo, tenantId, businessId,
                businessServiceCode, workflow, wfModuleName);
        ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(requestInfo,
                Collections.singletonList(processInstance));
        State state = callWorkFlow(workflowRequest);
        return state.getApplicationStatus();
    }

    private ProcessInstance getProcessInstanceForWorkflow(RequestInfo requestInfo, String tenantId, String businessId,
                                                          String businessServiceCode,
                                                          Workflow workflow,
                                                          String wfModuleName) {
        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setBusinessId(businessId);
        processInstance.setAction(workflow.getAction());
        processInstance.setModuleName(wfModuleName);
        processInstance.setTenantId(tenantId);
        processInstance.setBusinessService(getBusinessService(requestInfo, tenantId, businessServiceCode).getBusinessService());
        processInstance.setComment(workflow.getComment());
        if (!CollectionUtils.isEmpty(workflow.getAssignes())) {
            List<User> users = workflow.getAssignes().stream().map(uuid -> {
                User user = new User();
                user.setUuid(uuid);
                return user;
            }).collect(Collectors.toList());
            processInstance.setAssignes(users);
        }
        return processInstance;
    }

    private State callWorkFlow(ProcessInstanceRequest workflowReq) {
        StringBuilder url = new StringBuilder(configs.getWfHost()).append(configs.getWfTransitionPath());
        Object result = repository.fetchResult(url, workflowReq);
        ProcessInstanceResponse response = mapper.convertValue(result, ProcessInstanceResponse.class);
        return response.getProcessInstances().get(0).getState();
    }
}
