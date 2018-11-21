package uk.gov.cslearning.catalogue.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.catalogue.domain.SearchPage;
import uk.gov.cslearning.catalogue.repository.CourseRepository;

@RestController
@RequestMapping("/search")
public class SearchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

    private CourseRepository courseRepository;

    @Autowired
    public SearchController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping("/courses")
    public ResponseEntity<SearchResults> search(String query, FilterParameters filterParameters, PageParameters pageParameters) {
        LOGGER.debug("Searching courses with query {}", query);

        Pageable pageable = pageParameters.getPageRequest();
        SearchPage searchPage = courseRepository.search(query, pageable, filterParameters);

        return ResponseEntity.ok(new SearchResults(searchPage, pageable));
    }
}
