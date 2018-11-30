
package uk.gov.cslearning.catalogue.domain;

import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry.Option;
import org.springframework.data.domain.Page;

public class SearchPage {

    private Option topScoringSuggestion;

    private Page<Course> courses;

    public Option getTopScoringSuggestion() {
        return topScoringSuggestion;
    }

    public void setTopScoringSuggestion(Option topScoringSuggestion) {
        this.topScoringSuggestion = topScoringSuggestion;
    }

    public Page<Course> getCourses() {
        return courses;
    }

    public void setCourses(Page<Course> course) {
        this.courses = course;
    }

    public String getSuggestedText() {
        if (topScoringSuggestion == null || topScoringSuggestion.getText() == null) {
            return null;
        } else {
            return topScoringSuggestion.getText().toString();
        }
    }

    public long getTotalResults() {
        if (getCourses() == null) {
            return 0;
        } else {
            return getCourses().getTotalElements();
        }
    }
}
