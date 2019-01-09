package uk.gov.cslearning.catalogue.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.gov.cslearning.catalogue.domain.Course;

public interface CustomCourseRepository {
    Page<Course> findSuggested(String department, String areaOfWork, String interest, String status, Pageable pageable);
}
