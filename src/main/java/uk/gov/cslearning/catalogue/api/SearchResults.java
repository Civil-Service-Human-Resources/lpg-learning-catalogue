package uk.gov.cslearning.catalogue.api;

import uk.gov.cslearning.catalogue.domain.Course;

import java.util.List;

public class SearchResults {

    private String suggestion;

    private List<Course> results;

    public SearchResults(List<Course> results) {
        this.results = results;
    }

    public List<Course> getResults() {
        return results;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}
