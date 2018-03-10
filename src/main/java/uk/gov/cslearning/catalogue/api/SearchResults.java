package uk.gov.cslearning.catalogue.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class SearchResults<T> extends PageResults<T> {

    private String suggestion;

    SearchResults(Page<T> results, Pageable pageable) {
        super(results, pageable);
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}
