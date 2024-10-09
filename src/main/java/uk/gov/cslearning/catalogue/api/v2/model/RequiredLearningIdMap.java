package uk.gov.cslearning.catalogue.api.v2.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class RequiredLearningIdMap {

    private Map<String, List<String>> departmentCodeMap;

}
