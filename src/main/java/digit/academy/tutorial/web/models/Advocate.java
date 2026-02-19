package digit.academy.tutorial.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "Advocate registration application")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Advocate {
    @JsonProperty("id")
    @Size(max = 64)
    private String id;

    @JsonProperty("tenantId")
    @Size(max = 128)
    private String tenantId;

    @JsonProperty("applicationNumber")
    @Size(max = 128)
    private String applicationNumber;

    @JsonProperty("barRegistrationNumber")
    @Size(max = 128)
    private String barRegistrationNumber;

    @JsonProperty("advocateType")
    @Size(max = 64)
    private String advocateType;

    @JsonProperty("organisationID")
    @Size(max = 64)
    private String organisationID;

    @JsonProperty("individualId")
    @Size(max = 64)
    private String individualId;

    @JsonProperty("isActive")
    private Boolean isActive = true;

    @JsonProperty("workflow")
    @Valid
    private Workflow workflow;

    @JsonProperty("documents")
    @Valid
    private List<Document> documents;

    @JsonProperty("auditDetails")
    @Valid
    private AuditDetails auditDetails;

    @JsonProperty("additionalDetails")
    private Object additionalDetails;
}
