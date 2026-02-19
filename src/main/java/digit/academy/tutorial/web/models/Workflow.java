package digit.academy.tutorial.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Workflow {
    @JsonProperty("action")
    @NotNull
    private String action;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("assignes")
    private List<String> assignes;

    @JsonProperty("status")
    private String status;

    @JsonProperty("documents")
    @Valid
    private List<Document> documents;
}
