package uk.gov.cslearning.catalogue.api;

import uk.gov.cslearning.catalogue.domain.Course;

import java.util.List;

public class SearchResults {

    private List<Course> results;

    public SearchResults(List<Course> results) {
        this.results = results;
    }

    public List<Course> getResults() {
        return results;
    }
}
