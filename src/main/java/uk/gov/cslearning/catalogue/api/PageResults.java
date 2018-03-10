package uk.gov.cslearning.catalogue.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.gov.cslearning.catalogue.domain.Course;

import java.util.List;

public class PageResults<T> {

    private List<T> results;

    private Integer page;

    private Integer totalPages;

    private Integer size;

    PageResults(List<T> results) {
        this.results = results;
    }

    PageResults(Page<T> page, Pageable pageable) {
        this.results = page.getContent();
        this.page = pageable.getPageNumber();
        this.size = pageable.getPageSize();
        this.totalPages = (int) Math.ceil(page.getTotalElements() / (double) pageable.getPageSize());
    }

    public List<T> getResults() {
        return results;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public Integer getSize() {
        return size;
    }
}
