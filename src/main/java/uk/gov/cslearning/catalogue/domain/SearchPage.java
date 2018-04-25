 
package uk.gov.cslearning.catalogue.domain;

import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry.Option;
import org.springframework.data.domain.Page;

public class SearchPage {

    private Option topScoringSuggestion;

    private Page<Resource> resources;

    public Option getTopScoringSuggestion() {
        return topScoringSuggestion;
    }

    public void setTopScoringSuggestion(Option topScoringSuggestion) {
        this.topScoringSuggestion = topScoringSuggestion;
    }

    public Page<Resource> getResources() {
        return resources;
    }

    public void setResources(Page<Resource> resources) {
        this.resources = resources;
    }

    public String getSuggestedText() {
        if (topScoringSuggestion == null || topScoringSuggestion.getText() == null) {
            return null;
        } else {
            return topScoringSuggestion.getText().toString();
        }
    }

    public long getTotalResults() {
        if(getResources() == null){
            return 0;
        } else {
            return getResources().getTotalElements();
        }
    }
}
