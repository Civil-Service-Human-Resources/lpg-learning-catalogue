
package uk.gov.cslearning.catalogue.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.gov.cslearning.catalogue.domain.Course;

public class SearchResults extends PageResults<Course> {

    public SearchResults(Page<Course> searchPage, Pageable pageable) {
        super(searchPage, pageable);
    }

}
