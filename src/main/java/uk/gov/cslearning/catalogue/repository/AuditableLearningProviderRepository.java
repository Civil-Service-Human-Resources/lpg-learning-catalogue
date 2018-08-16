package uk.gov.cslearning.catalogue.repository;

import org.springframework.stereotype.Repository;
import uk.gov.cslearning.catalogue.domain.LearningProvider;
import uk.gov.cslearning.catalogue.service.AuthenticationFacade;

@Repository
public class AuditableLearningProviderRepository extends AuditableRepository<LearningProvider, LearningProviderRepository> {
    public AuditableLearningProviderRepository(LearningProviderRepository wrappedRepository, AuthenticationFacade authenticationFacade) {
        super(wrappedRepository, authenticationFacade);
    }
}
