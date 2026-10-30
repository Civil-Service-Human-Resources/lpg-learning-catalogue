package uk.gov.cslearning.catalogue.api.v2.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cslearning.catalogue.domain.Status;
import uk.gov.cslearning.catalogue.domain.Visibility;

import jakarta.validation.constraints.Pattern;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseSearchParameters {
    String query = "";

    Collection<Status> status = Collections.emptyList();
    Collection<Visibility> visibility = Collections.emptyList();
    List<String> types = Collections.emptyList();
    String cost;

    List<String> courseIds = Collections.emptyList();
    List<String> departments = Collections.emptyList();
    List<String> areasOfWork = Collections.emptyList();
    List<String> interests = Collections.emptyList();

    @Pattern(regexp = "^[a-zA-Z0-9]$")
    String titleStartsWith;

    public boolean hasModuleTypes() {
        return this.types != null && !this.types.isEmpty();
    }

    public boolean hasAudienceFields() {
        return (departments != null && !departments.isEmpty())
                || (areasOfWork != null && !areasOfWork.isEmpty())
                || (interests != null && !interests.isEmpty());
    }

    public boolean costIsFree() {
        return this.cost != null && this.cost.equals("free");
    }
}
