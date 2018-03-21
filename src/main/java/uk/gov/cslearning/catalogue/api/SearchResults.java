package uk.gov.cslearning.catalogue.api;

import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.SearchPage;

public class SearchResults<T> extends PageResults<Course> {

    private String suggestion;

    public SearchResults(SearchPage searchPage) {
        super(searchPage.getCourses().getContent());
        suggestion = searchPage.getTopScoringSuggestion().getText().toString();
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}
