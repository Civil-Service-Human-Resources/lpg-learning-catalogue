package uk.gov.cslearning.catalogue.api;

import org.springframework.data.domain.Pageable;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.SearchPage;

public class SearchResults<T> extends PageResults<Course> {

    private Integer page;

    private Integer size;

    private String suggestion;

    private Long totalResults;

    public SearchResults(SearchPage searchPage, Pageable pageable) {
        super(searchPage.getCourses().getContent());
        suggestion = searchPage.getSuggestedText();

        this.page = pageable.getPageNumber();
        this.size = pageable.getPageSize();
        this.totalResults = searchPage.getTotalResults();
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public Integer getPage() {
        return page;
    }

    public Long getTotalResults() {
        return totalResults;
    }

    public Integer getSize() {
        return size;
    }
}
