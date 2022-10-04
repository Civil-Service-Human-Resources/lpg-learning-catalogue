package uk.gov.cslearning.catalogue;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;

import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static <T> Page<T> searchPageToPage (SearchHits<T> searchHits, Pageable pageable) {
        List<T> content = searchHits.stream().map(hit -> hit.getContent()).collect(Collectors.toList());
        return new PageImpl<T>(content, pageable, searchHits.getTotalHits());
    }
}
