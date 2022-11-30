package uk.gov.cslearning.catalogue;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static <T> Page<T> searchPageToPage (SearchHits<T> searchHits, Pageable pageable) {
        List<T> content = searchHits.stream().map(hit -> hit.getContent()).collect(Collectors.toList());
        return new PageImpl<T>(content, pageable, searchHits.getTotalHits());
    }

    public static boolean hasRole (String roleName)
    {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(roleName));
    }

    public static boolean hasRoles (String[] roleNames)
    {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> Arrays.stream(roleNames).anyMatch(roleName -> grantedAuthority.getAuthority().equals(roleName)));
    }
}
