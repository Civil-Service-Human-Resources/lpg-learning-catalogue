package uk.gov.cslearning.catalogue.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.repository.CourseRepository;

import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private CourseRepository courseRepository;

    @Autowired
    public CourseController(CourseRepository courseRepository) {
        checkArgument(courseRepository != null);
        this.courseRepository = courseRepository;
    }

    @RequestMapping(method = POST)
    public ResponseEntity<Void> create(@RequestBody Course course, UriComponentsBuilder builder) {
        Course newCourse = courseRepository.save(course);
        return ResponseEntity.created(builder.path("/courses/{courseId}").build(newCourse.getId())).build();
    }

    @RequestMapping(method = GET)
    public ResponseEntity<SearchResults> listMandatory(@RequestParam("department") String department) {
        List<Course> mandatoryCourses = courseRepository.findMandatoryForDepartment(department);
        return ResponseEntity.ok(new SearchResults(mandatoryCourses.stream().map(CourseSummary::new).collect(toList())));
    }

    @RequestMapping("/{courseId}")
    public ResponseEntity<Course> get(@PathVariable("courseId") String courseId) {
        Optional<Course> result = courseRepository.findById(courseId);
        return result
                .map(course -> new ResponseEntity<>(course, OK))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }
}
