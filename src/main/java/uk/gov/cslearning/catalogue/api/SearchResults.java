
package uk.gov.cslearning.catalogue.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.SearchPage;

public class SearchResults extends PageResults<Course> {

    private String suggestion;

    @Autowired
    public SearchResults(SearchPage searchPage, Pageable pageable) {
        super(searchPage.getCourses(), pageable);
        suggestion = searchPage.getSuggestedText();
    }

    public String getSuggestion() {
        return suggestion;
    }

}
