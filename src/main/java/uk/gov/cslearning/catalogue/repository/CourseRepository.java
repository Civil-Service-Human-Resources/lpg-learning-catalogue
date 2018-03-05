package uk.gov.cslearning.catalogue.repository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.catalogue.domain.Course;

import java.util.List;

@Repository
public interface CourseRepository extends ElasticsearchRepository<Course, String> {

    @Query("{ \"terms\": { \"tags\": [ \"mandatory:all\", \"mandatory:?0\" ] } }")
    List<Course> findMandatory(String department);

    // FIXME: query should ignore mandatory
    @Query("{ \"terms\": { \"tags\": [ \"department:?0\", \"area-of-work:?1\" ] } }")
    List<Course> findSuggested(String department, String areaOfWork);
}
