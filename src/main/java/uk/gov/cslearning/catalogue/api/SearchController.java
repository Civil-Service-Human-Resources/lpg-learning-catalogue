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
import uk.gov.cslearning.catalogue.domain.SearchPage;
import uk.gov.cslearning.catalogue.repository.CourseRepository;

import static com.google.common.base.Preconditions.checkArgument;

@RestController
@RequestMapping("/search")
public class SearchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

    private CourseRepository courseRepository;

    @Autowired
    public SearchController(CourseRepository courseRepository) {
        checkArgument(courseRepository != null);
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public ResponseEntity<SearchResults<Course>> search(String query, PageParameters pageParameters) {
        LOGGER.debug("Searching courses with query {}", query);
        Pageable pageable = pageParameters.getPageRequest();

        SearchPage searchPage = courseRepository.search(query, pageable);

        SearchResults<Course> searchResults = new SearchResults(searchPage, pageable);

        return ResponseEntity.ok(searchResults);
    }
}
