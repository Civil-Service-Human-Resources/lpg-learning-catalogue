package uk.gov.cslearning.catalogue.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.repository.CourseRepository;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private CourseRepository courseRepository;

    @Autowired
    public CourseController(CourseRepository courseRepository) {
        checkArgument(courseRepository != null);
        this.courseRepository = courseRepository;
    }

    @RequestMapping("/{courseId}")
    public ResponseEntity<Course> get(@PathVariable("courseId") String courseId) {

        Optional<Course> result = courseRepository.findById(courseId);

        return result
                .map(course -> new ResponseEntity<>(course, OK))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }
}
