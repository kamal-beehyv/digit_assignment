package digit.academy.tutorial.enrichment;

import digit.academy.tutorial.util.IdgenUtil;
import digit.academy.tutorial.web.models.Advocate;
import digit.academy.tutorial.web.models.AdvocateRequest;
import digit.academy.tutorial.web.models.AuditDetails;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AdvocateEnrichment {

    @Autowired
    private IdgenUtil idgenUtil;

    @Value("${advocate.idgen.idname:advocate.applicationnumber}")
    private String advocateIdName;

    public void enrichCreate(AdvocateRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        List<Advocate> advocates = request.getAdvocates();
        String tenantId = advocates.get(0).getTenantId();
        List<String> idList = idgenUtil.getIdList(requestInfo, tenantId, advocateIdName, "", advocates.size());
        int index = 0;
        for (Advocate advocate : advocates) {
            advocate.setId(UUID.randomUUID().toString());
            advocate.setApplicationNumber(idList.get(index++));
            AuditDetails audit = AuditDetails.builder()
                    .createdBy(requestInfo.getUserInfo() != null ? requestInfo.getUserInfo().getUuid() : null)
                    .createdTime(System.currentTimeMillis())
                    .lastModifiedBy(requestInfo.getUserInfo() != null ? requestInfo.getUserInfo().getUuid() : null)
                    .lastModifiedTime(System.currentTimeMillis())
                    .build();
            advocate.setAuditDetails(audit);
            if (advocate.getIsActive() == null) advocate.setIsActive(true);
        }
    }

    public void enrichUpdate(AdvocateRequest request) {
        Advocate advocate = request.getAdvocates().get(0);
        if (advocate.getAuditDetails() != null) {
            advocate.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
            advocate.getAuditDetails().setLastModifiedBy(
                    request.getRequestInfo().getUserInfo() != null
                            ? request.getRequestInfo().getUserInfo().getUuid()
                            : null);
        }
    }
}
