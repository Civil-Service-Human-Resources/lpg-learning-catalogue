package uk.gov.cslearning.catalogue.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.gov.cslearning.catalogue.api.v2.model.GetCoursesParameters;
import uk.gov.cslearning.catalogue.domain.Course;

import java.util.List;

public interface CourseSuggestionsRepository {
    Page<Course> findSuggested(List<String> department, String areaOfWork, String interest, String status, String grade, Pageable pageable);
    Page<Course> findSuggested(GetCoursesParameters parameters);
}
