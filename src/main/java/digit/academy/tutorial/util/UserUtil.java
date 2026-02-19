package digit.academy.tutorial.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import digit.academy.tutorial.config.Configuration;
import digit.academy.tutorial.repository.ServiceRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fetches user details (including roles) from egov-user so RequestInfo can be
 * enriched before calling workflow. Workflow validates that the user has a role
 * allowed for the action (e.g. VERIFY requires ADVOCATE_VERIFIER); if the
 * client sends only userInfo.uuid without roles, workflow gets empty roles
 * and returns INVALID ROLE. Enriching here fixes that.
 */
@Component
@Slf4j
public class UserUtil {

    private final Configuration config;
    private final ServiceRequestRepository repository;
    private final ObjectMapper mapper;

    public UserUtil(Configuration config, ServiceRequestRepository repository, ObjectMapper mapper) {
        this.config = config;
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * If RequestInfo has userInfo with uuid but no roles, fetches the user from
     * user service and sets the full user (with roles) on requestInfo.
     *
     * @param requestInfo request containing userInfo (may have only uuid)
     * @param tenantId    tenant for user search (e.g. from advocate)
     */
    public void enrichUserInfoWithRoles(RequestInfo requestInfo, String tenantId) {
        if (requestInfo == null || requestInfo.getUserInfo() == null) return;
        if (requestInfo.getUserInfo().getUuid() == null || requestInfo.getUserInfo().getUuid().isBlank()) return;
        if (requestInfo.getUserInfo().getRoles() != null && !requestInfo.getUserInfo().getRoles().isEmpty()) return;

        String uuid = requestInfo.getUserInfo().getUuid();
        User fullUser = fetchUserByUuid(requestInfo, uuid, tenantId);
        if (fullUser != null) {
            requestInfo.setUserInfo(fullUser);
        }
    }

    /**
     * Calls user service search by uuid and returns User with roles, or null if not found / error.
     */
    public User fetchUserByUuid(RequestInfo requestInfo, String uuid, String tenantId) {
        StringBuilder url = new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint());
        Map<String, Object> request = new HashMap<>();
        request.put("RequestInfo", requestInfo);
        request.put("uuid", Collections.singletonList(uuid));
        if (tenantId != null && !tenantId.isBlank()) {
            request.put("tenantId", tenantId);
        }
        try {
            Object response = repository.fetchResult(url, request);
            if (response == null) return null;
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> userList = (List<Map<String, Object>>) ((Map<String, Object>) response).get("user");
            if (CollectionUtils.isEmpty(userList)) return null;
            return mapper.convertValue(userList.get(0), User.class);
        } catch (ServiceCallException e) {
            log.warn("User service error for uuid {}: {}", uuid, e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("Could not fetch user {} from user service: {}", uuid, e.getMessage());
            return null;
        }
    }
}
