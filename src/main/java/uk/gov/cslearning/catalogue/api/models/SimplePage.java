package uk.gov.cslearning.catalogue.api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SimplePage<T> implements Page<T> {

    private final Page<T> delegate;

    @JsonCreator
    public SimplePage(@JsonProperty("content") final List<T> content,
                      @JsonProperty("totalElements") final long totalElements,
                      @JsonProperty("totalPages") final int totalPages,
                      @JsonProperty("page") final int page,
                      @JsonProperty("size") final int size,
                      @JsonProperty("sort") final List<String> sort) {

        this.delegate = new PageImpl<>(content, PageRequest.of(page, size, Sort.by(sort != null ? sort.stream()
                .map(el -> el.split(","))
                .map(ar -> new Sort.Order(Sort.Direction.fromString(ar[1]), ar[0]))
                .collect(Collectors.toList()) : List.of())), totalElements);
    }

    public SimplePage(final List<T> content, final long totalElements, final Pageable pageable) {
        this.delegate = new PageImpl<>(content, pageable, totalElements);
    }

    public SimplePage(Page<T> delegate) {
        this.delegate = delegate;
    }

    public int getPage() {
        return getNumber();
    }

    @Override
    public long getTotalElements() {
        return delegate.getTotalElements();
    }

    public long getTotalResults() {
        return getTotalElements();
    }

    @JsonProperty("sort")
    public List<String> getSortList() {
        return getSort().stream()
                .map(order -> order.getProperty() + "," + order.getDirection().name())
                .collect(Collectors.toList());
    }

    @Override
    public int getTotalPages() {
        return delegate.getTotalPages();
    }

    @Override
    public <U> Page<U> map(Function<? super T, ? extends U> converter) {
        return new SimplePage<>(delegate.map(converter));
    }

    @Override
    public int getNumber() {
        return delegate.getNumber();
    }

    @Override
    public int getSize() {
        return delegate.getSize();
    }

    @Override
    public int getNumberOfElements() {
        return delegate.getNumberOfElements();
    }

    @Override
    public List<T> getContent() {
        return delegate.getContent();
    }

    @Override
    public boolean hasContent() {
        return delegate.hasContent();
    }

    @Override
    public Sort getSort() {
        return delegate.getSort();
    }

    @Override
    public boolean isFirst() {
        return delegate.isFirst();
    }

    @Override
    public boolean isLast() {
        return delegate.isLast();
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public boolean hasPrevious() {
        return delegate.hasPrevious();
    }

    @Override
    public Pageable nextPageable() {
        return delegate.nextPageable();
    }

    @Override
    public Pageable previousPageable() {
        return delegate.previousPageable();
    }

    @Override
    public Pageable getPageable() {
        return delegate.getPageable();
    }

    @Override
    public Iterator<T> iterator() {
        return delegate.iterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SimplePage<?> other)) return false;
        return delegate.equals(other.delegate);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
