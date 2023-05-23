package uk.gov.cslearning.catalogue.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PageResults<T> {

    private List<T> results;

    private Integer page;

    private Long totalResults;

    private Integer size;

    PageResults(List<T> results) {
        this.results = results;
    }

    public PageResults(Page<T> page, Pageable pageable) {
        this.results = page.getContent();
        this.page = pageable.getPageNumber();
        this.size = pageable.getPageSize();
        this.totalResults = page.getTotalElements();
    }

    public List<T> getResults() {
        return results;
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
