package digit.academy.tutorial.service;

import digit.academy.tutorial.config.ServiceConstants;
import digit.academy.tutorial.util.WorkflowUtil;
import digit.academy.tutorial.web.models.Advocate;
import digit.academy.tutorial.web.models.AdvocateRequest;
import digit.academy.tutorial.web.models.Workflow;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService {

    @Autowired
    private WorkflowUtil workflowUtil;

    @Value("${is.workflow.enabled:true}")
    private Boolean isWorkflowEnabled;

    public void updateWorkflowStatus(AdvocateRequest request) {
        if (!Boolean.TRUE.equals(isWorkflowEnabled)) return;
        RequestInfo requestInfo = request.getRequestInfo();
        Advocate advocate = request.getAdvocates().get(0);
        String tenantId = advocate.getTenantId();
        String businessId = advocate.getApplicationNumber();
        Workflow wf = advocate.getWorkflow();
        if (wf == null) return;
        String status = workflowUtil.updateWorkflowStatus(requestInfo, tenantId, businessId,
                ServiceConstants.ADVOCATE_REGISTRATION, wf, ServiceConstants.ADVOCATE_MODULE);
        advocate.getWorkflow().setStatus(status);
    }
}
