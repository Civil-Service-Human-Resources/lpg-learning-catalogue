package uk.gov.cslearning.catalogue.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.Resource;
import uk.gov.cslearning.catalogue.domain.SearchPage;
import uk.gov.cslearning.catalogue.repository.CourseRepository;
import uk.gov.cslearning.catalogue.repository.ResourceRepository;

import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkArgument;

@RestController
@RequestMapping("/search")
public class SearchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

    private ResourceRepository resourceRepository;

    private CourseRepository courseRepository;

    @Autowired
    public SearchController(ResourceRepository resourceRepository, CourseRepository courseRepository) {
        checkArgument(resourceRepository != null);
        this.resourceRepository = resourceRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public ResponseEntity<SearchResults> search(String query, FilterParameters filterParameters, PageParameters pageParameters) {
        LOGGER.debug("Searching resources with query {}", query);

        Pageable pageable = pageParameters.getPageRequest();
        SearchPage searchPage = resourceRepository.search(query, pageable, filterParameters);

        return ResponseEntity.ok(new SearchResults(searchPage, pageable));
    }

    @GetMapping("/create")
    public ResponseEntity create() {
        LOGGER.debug("Creating search indexes");
        Iterable<Course> courses = courseRepository.findAll();
        resourceRepository.deleteAll();
        for (Course course : courses) {
            ArrayList<Resource> resources = Resource.fromCourse(course);
            for (Resource resource : resources) {
                LOGGER.debug("Creating resource {}", resource);
                resourceRepository.save(resource);
            }
        }
        return ResponseEntity.noContent().build();
    }
}
