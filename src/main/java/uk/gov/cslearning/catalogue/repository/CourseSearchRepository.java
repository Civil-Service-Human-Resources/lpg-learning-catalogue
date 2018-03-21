package uk.gov.cslearning.catalogue.repository;

import uk.gov.cslearning.catalogue.domain.SearchPage;

public interface CourseSearchRepository {

    SearchPage search(String query);
}
