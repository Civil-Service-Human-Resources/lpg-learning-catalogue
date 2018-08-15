package uk.gov.cslearning.catalogue.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.service.AuthenticationFacade;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AuditableLearningProviderRepositoryTest {
    @Mock
    private LearningProviderRepository wrappedRepository;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @InjectMocks
    private AuditableLearningProviderRepository auditableLearningProviderRepository;

    @Test
    public void refreshShouldCallWrappedRepository() {
        auditableLearningProviderRepository.refresh();
        verify(wrappedRepository).refresh();
    }
}