package uk.gov.cslearning.catalogue.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseController.class);

    private CourseRepository courseRepository;

    @Autowired
    public CourseController(CourseRepository courseRepository) {
        checkArgument(courseRepository != null);
        this.courseRepository = courseRepository;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody Course course, UriComponentsBuilder builder) {
        LOGGER.debug("Creating course {}", course);
        Course newCourse = courseRepository.save(course);
        return ResponseEntity.created(builder.path("/courses/{courseId}").build(newCourse.getId())).build();
    }

    @GetMapping(params = { "mandatory", "department" })
    public ResponseEntity<SearchResults> listMandatory(@RequestParam("department") String department) {
        LOGGER.debug("Listing mandatory courses for department {}", department);
        List<Course> courses = courseRepository.findMandatory(department);
        return ResponseEntity.ok(new SearchResults(courses));
    }

    @GetMapping(params = { "department", "areaOfWork" })
    public ResponseEntity<SearchResults> listSuggested(@RequestParam("department") String department,
                                                       @RequestParam("areaOfWork") String areaOfWork) {
        LOGGER.debug("Listing suggested courses for department {} and area of work {}", department, areaOfWork);
        List<Course> courses = courseRepository.findSuggested(department, areaOfWork, PageRequest.of(0, 100));
        return ResponseEntity.ok(new SearchResults(courses));
    }

    @GetMapping
    public ResponseEntity<SearchResults> listAll() {
        LOGGER.debug("Listing all courses");
        Iterable<Course> courses = courseRepository.findAll();
        return ResponseEntity.ok(new SearchResults(stream(courses.spliterator(), false)
                .collect(toList())));
    }

    @PutMapping(path = "/{courseId}")
    public ResponseEntity<Void> update(@PathVariable("courseId") String courseId, @RequestBody Course course) {
        LOGGER.debug("Updating course {}", course);
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
        LOGGER.debug("Getting course with ID {}", courseId);
        Optional<Course> result = courseRepository.findById(courseId);
        return result
                .map(course -> new ResponseEntity<>(course, OK))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }
}
