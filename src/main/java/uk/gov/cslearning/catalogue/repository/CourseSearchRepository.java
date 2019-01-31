package uk.gov.cslearning.catalogue.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.gov.cslearning.catalogue.api.FilterParameters;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.LearningProvider;
import uk.gov.cslearning.catalogue.domain.SearchPage;
import uk.gov.cslearning.catalogue.domain.Status;

import java.util.Collection;
import java.util.List;

public interface CourseSearchRepository {
    SearchPage search(String query, Pageable pageable, FilterParameters filterParameters, Collection<Status> status);

    Page<Course> findAllByOrganisationCode(String organisationalUnitCode, Pageable pageable);

    Page<Course> findAllByProfessionId(String professionId, Pageable pageable);

    List<Course> findAllByProfessionId(String professionId);

    List<Course> findAllByLearningProvider(LearningProvider learningProvider);
}
