package uk.gov.cslearning.catalogue.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.catalogue.domain.Course;

@Repository
public interface CourseRepository extends ElasticsearchRepository<Course, String> {

    Course save(Course course);

    void delete(Course course);

//    Course findOne(String id);
}
