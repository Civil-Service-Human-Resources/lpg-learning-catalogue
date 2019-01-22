package uk.gov.cslearning.catalogue.repository;

import org.springframework.data.domain.Pageable;
import uk.gov.cslearning.catalogue.api.FilterParameters;
import uk.gov.cslearning.catalogue.api.ProfileParameters;
import uk.gov.cslearning.catalogue.domain.SearchPage;
import uk.gov.cslearning.catalogue.domain.Status;

import java.util.Collection;

public interface CourseSearchRepository {
    SearchPage search(String query, Pageable pageable, FilterParameters filterParameters, ProfileParameters profileParameters, Collection<Status> status, String visibility);
}
