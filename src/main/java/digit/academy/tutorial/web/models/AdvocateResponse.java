package digit.academy.tutorial.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;

import java.util.List;

@Schema(description = "Advocate API response")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdvocateResponse {
    @JsonProperty("responseInfo")
    @Valid
    private ResponseInfo responseInfo;

    @JsonProperty("advocates")
    @Valid
    private List<Advocate> advocates;

    @JsonProperty("pagination")
    @Valid
    private Pagination pagination;
}
