package uk.gov.cslearning.catalogue.exception;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.stream.Collectors;

public class InvalidSupplierException extends RuntimeException {
    public InvalidSupplierException(Collection<? extends GrantedAuthority> authorities) {
        super(String.format("Authority not found in authoritySupplierNameMap: %s",
                authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(", "))));
    }
}
