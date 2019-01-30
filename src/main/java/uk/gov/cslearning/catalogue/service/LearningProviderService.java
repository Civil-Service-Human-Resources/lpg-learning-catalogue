package uk.gov.cslearning.catalogue.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.LearningProvider;
import uk.gov.cslearning.catalogue.exception.InvalidSupplierException;
import uk.gov.cslearning.catalogue.exception.LearningProviderNotFoundException;
import uk.gov.cslearning.catalogue.repository.LearningProviderRepository;

import java.util.Map;
import java.util.Set;

@Service
public class LearningProviderService {
    private final Map<String, String> authoritySupplierNameMap;
    private final LearningProviderRepository learningProviderRepository;

    public LearningProviderService(@Qualifier("authoritySupplierNameMap") Map<String, String> authoritySupplierNameMap,
                                   LearningProviderRepository learningProviderRepository) {
        this.authoritySupplierNameMap = authoritySupplierNameMap;
        this.learningProviderRepository = learningProviderRepository;
    }

    public String getLearningProviderNameFromAuthentication(Authentication authentication) {
        Set<String> authorities = authoritySupplierNameMap.keySet();

        String authority = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authorities::contains)
                .findFirst()
                .orElseThrow(() -> new InvalidSupplierException(authentication.getAuthorities()));

        return authoritySupplierNameMap.get(authority);
    }

    public LearningProvider findByName(String name) {
        return learningProviderRepository.findByName(name)
                .orElseThrow(() -> new LearningProviderNotFoundException("Learning provider not found: " + name));
    }
}
