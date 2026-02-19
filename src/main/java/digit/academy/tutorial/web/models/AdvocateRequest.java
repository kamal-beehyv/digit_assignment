package digit.academy.tutorial.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;

import java.util.List;

@Schema(description = "Advocate create/update request")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdvocateRequest {
    @JsonProperty("RequestInfo")
    @NotNull
    @Valid
    private RequestInfo requestInfo;

    @JsonProperty("advocates")
    @NotNull
    @Valid
    private List<Advocate> advocates;
}
