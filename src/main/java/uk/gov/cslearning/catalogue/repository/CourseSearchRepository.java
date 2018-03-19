package uk.gov.cslearning.catalogue.repository;

import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.SearchPage;

import java.util.List;

public interface CourseSearchRepository {

    List<Course> search(String query);

    SearchPage suggestions(String suggestText);

}
