package uk.gov.cslearning.catalogue.repository;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.catalogue.domain.Course;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends ElasticsearchRepository<Course, String> {

    @Query("{ \"terms\": { \"tags\": [ \"mandatory:all\", \"mandatory:?0\" ] } }")
    List<Course> findMandatoryForDepartment(String department);
}
