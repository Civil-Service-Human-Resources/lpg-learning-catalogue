package uk.gov.cslearning.catalogue.api.v2.model;

import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

@Data
public class GetCoursesParameters {

    private static final String ELASTIC_EMPTY_PARAM = "NONE";
    private static final String COURSE_STATUS = "Published";

    String areaOfWork = ELASTIC_EMPTY_PARAM;
    List<String> departments = Collections.emptyList();
    String interest = ELASTIC_EMPTY_PARAM;
    String status = COURSE_STATUS;
    String grade = ELASTIC_EMPTY_PARAM;

    List<String> excludeAreasOfWork = Collections.emptyList();
    List<String> excludeInterests = Collections.emptyList();
    List<String> excludeDepartments = Collections.emptyList();
    List<String> excludeCourseIDs = Collections.emptyList();

    Pageable pageable;
}
