
package uk.gov.cslearning.catalogue.api;

import org.springframework.data.domain.Pageable;
import uk.gov.cslearning.catalogue.domain.Resource;
import uk.gov.cslearning.catalogue.domain.SearchPage;

public class SearchResults extends PageResults<Resource> {

    private String suggestion;

    public SearchResults(SearchPage searchPage, Pageable pageable) {
        super(searchPage.getResources(), pageable);
        suggestion = searchPage.getSuggestedText();
    }

    public String getSuggestion() {
        return suggestion;
    }
}
