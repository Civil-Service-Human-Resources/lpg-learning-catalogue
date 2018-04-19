package uk.gov.cslearning.catalogue.repository;

import org.springframework.data.domain.Pageable;
import uk.gov.cslearning.catalogue.api.FilterParameters;
import uk.gov.cslearning.catalogue.domain.SearchPage;

public interface CourseSearchRepository {

    SearchPage search(String query, Pageable pageable, FilterParameters filterParameters);
}
