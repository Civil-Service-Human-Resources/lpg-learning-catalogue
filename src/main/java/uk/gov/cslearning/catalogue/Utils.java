package uk.gov.cslearning.catalogue;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.gov.cslearning.catalogue.domain.validation.RoleSet;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static <T> Page<T> searchPageToPage (SearchHits<T> searchHits, Pageable pageable) {
        List<T> content = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
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

    public static boolean checkRoles(RoleSet roles) {
        List<String> userRoles = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return Arrays.stream(roles.getAllRoles()).allMatch(userRoles::contains) &&
        Arrays.stream(roles.getAnyRole()).anyMatch(userRoles::contains);
    }

}
