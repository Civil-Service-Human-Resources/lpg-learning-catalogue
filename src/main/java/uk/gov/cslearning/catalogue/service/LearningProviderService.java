package uk.gov.cslearning.catalogue.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.config.SupplierConfig;
import uk.gov.cslearning.catalogue.domain.LearningProvider;
import uk.gov.cslearning.catalogue.exception.InvalidSupplierException;
import uk.gov.cslearning.catalogue.exception.LearningProviderNotFoundException;
import uk.gov.cslearning.catalogue.repository.LearningProviderRepository;

import java.util.Map;
import java.util.Set;

@Service
public class LearningProviderService {
    private final SupplierConfig supplierConfig;
    private final LearningProviderRepository learningProviderRepository;

    public LearningProviderService(SupplierConfig supplierConfig, LearningProviderRepository learningProviderRepository) {
        this.supplierConfig = supplierConfig;
        this.learningProviderRepository = learningProviderRepository;
    }

    public String getLearningProviderNameFromAuthentication(Authentication authentication) {
        Map<String, String> authoritySupplierNameMap = supplierConfig.getReportingAuthorities();

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
