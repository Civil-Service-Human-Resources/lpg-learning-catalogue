package uk.gov.cslearning.catalogue.api;

import org.springframework.data.domain.Pageable;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.SearchPage;

public class SearchResults<T> extends PageResults<Course> {

    private String suggestion;

    public SearchResults(SearchPage searchPage, Pageable pageable) {
        super(searchPage.getCourses(), pageable);
        suggestion = searchPage.getSuggestedText();
    }

    public String getSuggestion() {
        return suggestion;
    }
}
