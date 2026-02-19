package digit.academy.tutorial.validators;

import digit.academy.tutorial.config.ServiceConstants;
import digit.academy.tutorial.repository.AdvocateRepository;
import digit.academy.tutorial.util.MdmsUtil;
import digit.academy.tutorial.web.models.Advocate;
import digit.academy.tutorial.web.models.AdvocateSearchCriteria;
import digit.academy.tutorial.web.models.AdvocateRequest;
import net.minidev.json.JSONArray;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

@Component
public class AdvocateValidator {

    @Autowired
    private AdvocateRepository repository;

    @Autowired
    private MdmsUtil mdmsUtil;

    public void validateCreateRequest(AdvocateRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        request.getAdvocates().forEach(advocate -> {
            if (ObjectUtils.isEmpty(advocate.getTenantId())) {
                throw new CustomException("ADVOCATE_TENANT_REQUIRED", "tenantId is mandatory for advocate registration");
            }
            if (ObjectUtils.isEmpty(advocate.getAdvocateType())) {
                throw new CustomException("ADVOCATE_TYPE_REQUIRED", "advocateType (from master) is mandatory");
            }
            validateAdvocateTypeFromMaster(requestInfo, advocate.getTenantId(), advocate.getAdvocateType());
        });
    }

    private void validateAdvocateTypeFromMaster(RequestInfo requestInfo, String tenantId, String advocateTypeCode) {
        Map<String, Map<String, JSONArray>> mdmsRes = mdmsUtil.fetchMdmsData(requestInfo, tenantId,
                ServiceConstants.MDMS_MODULE_ADVOCATE, List.of(ServiceConstants.MDMS_MASTER_ADVOCATE_TYPE));
        if (mdmsRes == null || mdmsRes.isEmpty()) return;
        Map<String, JSONArray> module = mdmsRes.get(ServiceConstants.MDMS_MODULE_ADVOCATE);
        if (module == null) return;
        JSONArray advocateTypes = module.get(ServiceConstants.MDMS_MASTER_ADVOCATE_TYPE);
        if (CollectionUtils.isEmpty(advocateTypes)) return;
        boolean found = advocateTypes.stream()
                .anyMatch(obj -> advocateTypeCode.equals(((Map<?, ?>) obj).get("code")));
        if (!found) {
            throw new CustomException("INVALID_ADVOCATE_TYPE", "advocateType must be a valid code from master: " + advocateTypeCode);
        }
    }

    public Advocate validateApplicationExistence(Advocate advocate) {
        AdvocateSearchCriteria criteria = AdvocateSearchCriteria.builder()
                .applicationNumber(advocate.getApplicationNumber())
                .tenantId(advocate.getTenantId())
                .build();
        var list = repository.getAdvocates(criteria);
        if (list == null || list.isEmpty()) {
            throw new CustomException("ADVOCATE_NOT_FOUND", "Advocate with applicationNumber " + advocate.getApplicationNumber() + " not found");
        }
        return list.get(0);
    }
}
