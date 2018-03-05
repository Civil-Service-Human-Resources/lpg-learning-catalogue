package uk.gov.cslearning.catalogue.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.repository.CourseRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
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

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody Course course, UriComponentsBuilder builder) {
        Course newCourse = courseRepository.save(course);
        return ResponseEntity.created(builder.path("/courses/{courseId}").build(newCourse.getId())).build();
    }

    @GetMapping(params = { "mandatory", "department" })
    public ResponseEntity<SearchResults> listMandatory(@RequestParam("department") String department) {
        List<Course> courses = courseRepository.findMandatory(department);
        return ResponseEntity.ok(new SearchResults(courses.stream().map(CourseSummary::new).collect(toList())));
    }

    @GetMapping(params = { "department", "areaOfWork" })
    public ResponseEntity<SearchResults> listSuggested(@RequestParam("department") String department,
                                                       @RequestParam("areaOfWork") String areaOfWork) {
        List<Course> courses = courseRepository.findSuggested(department, areaOfWork);
        return ResponseEntity.ok(new SearchResults(courses.stream().map(CourseSummary::new).collect(toList())));
    }

    @GetMapping
    public ResponseEntity<SearchResults> listAll() {
        Iterable<Course> courses = courseRepository.findAll();
        return ResponseEntity.ok(new SearchResults(stream(courses.spliterator(), false)
                .map(CourseSummary::new).collect(toList())));
    }

    @PutMapping(path = "/{courseId}")
    public ResponseEntity<Void> update(@PathVariable("courseId") String courseId, @RequestBody Course course) {
        if (!courseId.equals(course.getId())) {
            return ResponseEntity.badRequest().build();
        }
        if (!courseRepository.existsById(courseId)) {
            return ResponseEntity.badRequest().build();
        }
        courseRepository.save(course);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Course> get(@PathVariable("courseId") String courseId) {
        Optional<Course> result = courseRepository.findById(courseId);
        return result
                .map(course -> new ResponseEntity<>(course, OK))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }
}
