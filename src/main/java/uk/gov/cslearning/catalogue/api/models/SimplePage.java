package uk.gov.cslearning.catalogue.api.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Delegate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode
@ToString
@SuppressWarnings("unchecked")
public class SimplePage<T> implements Page<T> {

    @Delegate
    private final Page<T> delegate;

    @JsonCreator
    public SimplePage(@JsonProperty("content") final List<T> content,
                      @JsonProperty("totalElements") final long totalElements,
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

    public long getTotalResults() {
        return getTotalElements();
    }

    @JsonProperty("sort")
    public List<String> getSortList() {
        return getSort().stream()
                .map(order -> order.getProperty() + "," + order.getDirection().name())
                .collect(Collectors.toList());
    }
}
