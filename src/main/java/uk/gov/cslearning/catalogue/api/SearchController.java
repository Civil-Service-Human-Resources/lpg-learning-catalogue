 
package uk.gov.cslearning.catalogue.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.catalogue.domain.Resource;
import uk.gov.cslearning.catalogue.domain.SearchPage;
import uk.gov.cslearning.catalogue.repository.ResourceRepository;

import static com.google.common.base.Preconditions.checkArgument;

@RestController
@RequestMapping("/search")
public class SearchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

    private ResourceRepository resourceRepository;

    @Autowired
    public SearchController(ResourceRepository resourceRepository) {
        checkArgument(resourceRepository != null);
        this.resourceRepository = resourceRepository;
    }

    @GetMapping
    public ResponseEntity<SearchResults<Resource>> search(String query, FilterParameters filterParameters, PageParameters pageParameters) {
        LOGGER.debug("Searching resources with query {}", query);
        Pageable pageable = pageParameters.getPageRequest();

        SearchPage searchPage = resourceRepository.search(query, pageable,filterParameters);

        SearchResults<Resource> searchResults = new SearchResults<>(searchPage, pageable);

        return ResponseEntity.ok(searchResults);
    }
}
