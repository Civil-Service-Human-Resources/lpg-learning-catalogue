package uk.gov.cslearning.catalogue.api;

import java.util.List;

public class SearchResults {

    private List<CourseSummary> results;

    public SearchResults(List<CourseSummary> results) {
        this.results = results;
    }

    public List<CourseSummary> getResults() {
        return results;
    }
}
