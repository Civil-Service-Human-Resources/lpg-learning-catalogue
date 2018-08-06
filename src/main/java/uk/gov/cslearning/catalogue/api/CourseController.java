package uk.gov.cslearning.catalogue.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.Resource;
import uk.gov.cslearning.catalogue.domain.module.Module;
import uk.gov.cslearning.catalogue.repository.CourseRepository;
import uk.gov.cslearning.catalogue.repository.ResourceRepository;
import uk.gov.cslearning.catalogue.service.ModuleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang.StringUtils.defaultIfEmpty;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CourseController.class);

    private final CourseRepository courseRepository;

    private final ResourceRepository resourceRepository;

    private final ModuleService moduleService;

    @Autowired
    public CourseController(CourseRepository courseRepository, ResourceRepository resourceRepository, ModuleService moduleService) {
        this.courseRepository = courseRepository;
        this.resourceRepository = resourceRepository;
        this.moduleService = moduleService;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody Course course, UriComponentsBuilder builder) {
        LOGGER.debug("Creating course {}", course);
        Course newCourse = courseRepository.save(course);

        ArrayList<Resource> resources = Resource.fromCourse(newCourse);
        for (Resource resource : resources) {
            LOGGER.debug("Creating resource {}", resource);
            resourceRepository.save(resource);
        }

        return ResponseEntity.created(builder.path("/courses/{courseId}").build(newCourse.getId())).build();
    }

    @GetMapping(params = {"mandatory", "department"})
    public ResponseEntity<PageResults<Course>> listMandatory(@RequestParam("department") String department,
                                                             PageParameters pageParameters) {
        LOGGER.debug("Listing mandatory courses for department {}", department);
        Pageable pageable = pageParameters.getPageRequest();
        Page<Course> page = courseRepository.findMandatory(department, pageParameters.getPageRequest());
        return ResponseEntity.ok(new PageResults<>(page, pageable));
    }

    @GetMapping(params = "courseId")
    public ResponseEntity<Iterable<Course>> get(@RequestParam("courseId") List<String> courseIds) {
        LOGGER.debug("Getting courses with IDs {}", courseIds);
        Iterable<Course> result = courseRepository.findAllById(courseIds);
        return new ResponseEntity<>(result, OK);
    }

    @GetMapping
    public ResponseEntity<PageResults<Course>> list(@RequestParam(name = "areaOfWork", required = false) List<String> areasOfWork,
                                                    @RequestParam(name = "department", required = false) List<String> departments,
                                                    @RequestParam(name = "interest", required = false) List<String> interests,
                                                    PageParameters pageParameters) {
        LOGGER.debug("Listing courses");

        areasOfWork = defaultIfNull(areasOfWork, emptyList());
        departments = defaultIfNull(departments, emptyList());
        interests = defaultIfNull(interests, emptyList());

        Pageable pageable = pageParameters.getPageRequest();
        Page<Course> page;

        if (departments.isEmpty() && areasOfWork.isEmpty()) {
            page = courseRepository.findAll(pageable);
        } else {
            page = courseRepository.findSuggested(
                    defaultIfEmpty(String.join(",", departments), "none"),
                    defaultIfEmpty(String.join(",", areasOfWork), "none"),
                    defaultIfEmpty(String.join(",", interests), "none"),
                    pageable);
        }
        return ResponseEntity.ok(new PageResults<>(page, pageable));
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

        ArrayList<Resource> resources = Resource.fromCourse(course);
        for (Resource resource : resources) {
            LOGGER.debug("Creating resource {}", resource);
            resourceRepository.save(resource);
        }

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

    @PostMapping("/{courseId}/modules/")
    public ResponseEntity<Void> createModule(@PathVariable String courseId, @RequestBody Module module, UriComponentsBuilder builder) {
        LOGGER.debug("Adding module to course with ID {}", courseId);

        Module saved = moduleService.save(courseId, module);

        return ResponseEntity.created(builder.path("/courses/{courseId}/modules/{moduleId}").build(courseId, saved.getId())).build();
    }

    @GetMapping("/{courseId}/modules/{moduleId}")
    public ResponseEntity<Module> getModule(@PathVariable String courseId, @PathVariable String moduleId) {
        LOGGER.debug("Getting module {} of course {}", moduleId, courseId);

        Optional<Module> result = moduleService.find(courseId, moduleId);

        return result.map(module -> new ResponseEntity<>(module, OK))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }

    @GetMapping("/delete")
    public void delete() {

        courseRepository.deleteAll();

    }


}
