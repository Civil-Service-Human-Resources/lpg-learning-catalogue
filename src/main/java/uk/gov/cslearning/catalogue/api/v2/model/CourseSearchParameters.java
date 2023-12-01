package uk.gov.cslearning.catalogue.api.v2.model;

import java.util.Collections;
import java.util.List;
import lombok.Data;

@Data
public class CourseSearchParameters {
    String searchTerm = "";
    List<String> types = Collections.emptyList();
    String cost;
    List<String> departments = Collections.emptyList();
    List<String> areasOfWork = Collections.emptyList();
    List<String> interests = Collections.emptyList();

    public boolean hasAudienceFields(){
        return !departments.isEmpty() || !areasOfWork.isEmpty() || !areasOfWork.isEmpty();
    }

    public boolean costIsFree(){
        return this.cost != null && this.cost.equals("free");
    }
}
