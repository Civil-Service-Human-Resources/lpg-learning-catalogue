package uk.gov.cslearning.catalogue.repository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.catalogue.domain.Course;

import java.util.List;

@Repository
public interface CourseRepository extends ElasticsearchRepository<Course, String>, CourseSearchRepository {

    @Query("{ \"terms\": { \"tags\": [ \"mandatory:all\", \"mandatory:?0\" ] } }")
    List<Course> findMandatory(String department);

    @Query("{ \"bool\": { \"must\": [{ \"terms\": { \"tags.keyword\": [ \"area-of-work:all\", \"area-of-work:?1\", \"department:all\", \"department:?0\" ]}}], \"must_not\": [ { \"terms\": { \"tags.keyword\": [ \"mandatory:all\", \"mandatory:?0\" ]}}]}}")
    List<Course> findSuggested(String department, String areaOfWork);
}
