package digit.academy.tutorial.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import digit.academy.tutorial.config.Configuration;
import digit.academy.tutorial.repository.ServiceRequestRepository;
import org.egov.common.contract.idgen.IdGenerationRequest;
import org.egov.common.contract.idgen.IdGenerationResponse;
import org.egov.common.contract.idgen.IdRequest;
import org.egov.common.contract.idgen.IdResponse;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static digit.academy.tutorial.config.ServiceConstants.*;

@Component
public class IdgenUtil {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ServiceRequestRepository restRepo;

    @Autowired
    private Configuration configs;

    public List<String> getIdList(RequestInfo requestInfo, String tenantId, String idName, String idFormat, int count) {
        List<IdRequest> reqList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            reqList.add(IdRequest.builder().idName(idName).format(idFormat).tenantId(tenantId).build());
        }
        IdGenerationRequest request = IdGenerationRequest.builder().idRequests(reqList).requestInfo(requestInfo).build();
        StringBuilder uri = new StringBuilder(configs.getIdGenHost()).append(configs.getIdGenPath());
        Object result = restRepo.fetchResult(uri, request);
        if (result == null) {
            throw new CustomException(IDGEN_ERROR,
                    "IDGen service unavailable or returned an error. Ensure egov-idgen is running on "
                            + configs.getIdGenHost() + ", MDMS is running with mdms_data for tenant " + tenantId
                            + ", and egov-idgen has autocreate.new.seq=true or sequence SEQ_EG_ADVOCATE_APPLICATION_NUM exists in IDGen DB.");
        }
        IdGenerationResponse response = mapper.convertValue(result, IdGenerationResponse.class);
        List<IdResponse> idResponses = response.getIdResponses();
        if (CollectionUtils.isEmpty(idResponses)) {
            throw new CustomException(IDGEN_ERROR, NO_IDS_FOUND_ERROR);
        }
        return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
    }
}
