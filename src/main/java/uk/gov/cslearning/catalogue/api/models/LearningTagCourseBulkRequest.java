package uk.gov.cslearning.catalogue.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LearningTagCourseBulkRequest {
    @NotEmpty
    private List<Long> learningTagIds;
    @NotEmpty
    private List<String> courseIds;
}
