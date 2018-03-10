package uk.gov.cslearning.catalogue.api;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageParameters {

    private Integer page;

    private Integer size;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Pageable getPageRequest() {
        if (page != null && size != null) {
            return PageRequest.of(page, size);
        }
        return PageRequest.of(0, 10);
    }
}
