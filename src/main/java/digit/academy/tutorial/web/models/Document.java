package digit.academy.tutorial.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Document {
    @JsonProperty("id")
    @Size(max = 64)
    private String id;

    @JsonProperty("documentType")
    private String documentType;

    @JsonProperty("fileStore")
    private String fileStore;

    @JsonProperty("documentUid")
    @Size(max = 64)
    private String documentUid;

    @JsonProperty("additionalDetails")
    private Object additionalDetails;
}
