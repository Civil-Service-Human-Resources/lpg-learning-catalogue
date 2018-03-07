package uk.gov.cslearning.catalogue.repository;

import uk.gov.cslearning.catalogue.domain.Course;

import java.util.List;

public interface CourseSearchRepository {

    List<Course> search(String query);
}
