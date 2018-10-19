package uk.gov.cslearning.catalogue.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.catalogue.api.FilterParameters;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.SearchPage;
import uk.gov.cslearning.catalogue.domain.Status;

@Repository
public interface CourseRepository extends ElasticsearchRepository<Course, String>, ResourceSearchRepository{

    Page<Course> findAllByStatus(Status status, Pageable pageable);

    @Query("{ \"bool\": { \"must\": [{ \"match\": { \"modules.audiences.mandatory\": \"true\" } }, { \"term\": { \"modules.audiences.departments\": \"?0\" }}] }}")
    Page<Course> findMandatory(String department, Pageable pageable);

    @Query("{\"bool\": {\"should\": [{\"match\": {\"audiences.departments\": {\"query\": \"?0\",\"zero_terms_query\": \"none\"}}},{\"match\": {\"audiences.areasOfWork\": {\"query\": \"?1\",\"zero_terms_query\": \"none\"}}},{\"match\": {\"audiences.interests\": {\"query\": \"?2\",\"zero_terms_query\": \"none\"}}}],\"must\": [{\"match\": {\"status\": {\"query\": \"?3\"}}}],\"must_not\": [{\"match\": {\"audiences.type\": \"REQUIRED_LEARNING\"}}]}}")
    Page<Course> findSuggested(String department, String areaOfWork, String interest, String status, Pageable pageable);

    SearchPage search(String query, Pageable pageable, FilterParameters filterParameters);
}
