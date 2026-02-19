package digit.academy.tutorial.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.stereotype.Component;

import static digit.academy.tutorial.config.ServiceConstants.*;

@Component
public class ResponseInfoFactory {

    public ResponseInfo createResponseInfoFromRequestInfo(RequestInfo requestInfo, Boolean success) {
        String apiId = requestInfo != null ? requestInfo.getApiId() : "";
        String ver = requestInfo != null ? requestInfo.getVer() : "";
        Long ts = requestInfo != null ? requestInfo.getTs() : null;
        String msgId = requestInfo != null ? requestInfo.getMsgId() : "";
        String responseStatus = success ? SUCCESSFUL : FAILED;
        return ResponseInfo.builder()
                .apiId(apiId)
                .ver(ver)
                .ts(ts)
                .resMsgId(RES_MSG_ID)
                .msgId(msgId)
                .status(responseStatus)
                .build();
    }
}
