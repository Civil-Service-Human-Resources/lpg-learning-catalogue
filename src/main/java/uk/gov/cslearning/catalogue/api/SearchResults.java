package uk.gov.cslearning.catalogue.api;

import uk.gov.cslearning.catalogue.domain.CatalogueEntry;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class SearchResults {

    private List<CatalogueEntry> results;

    public SearchResults(List<CatalogueEntry> results) {
        checkArgument(results != null, "results is null");
        this.results = new ArrayList<>(results);
    }

    public List<CatalogueEntry> getResults() {
        return results;
    }
}
