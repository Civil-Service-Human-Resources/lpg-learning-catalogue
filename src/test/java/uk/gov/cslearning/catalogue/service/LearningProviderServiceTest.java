package uk.gov.cslearning.catalogue.service;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import uk.gov.cslearning.catalogue.domain.LearningProvider;
import uk.gov.cslearning.catalogue.exception.InvalidSupplierException;
import uk.gov.cslearning.catalogue.exception.LearningProviderNotFoundException;
import uk.gov.cslearning.catalogue.repository.LearningProviderRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class LearningProviderServiceTest {
    @Mock
    private LearningProviderRepository learningProviderRepository;

    private final Map<String, String> authoritySupplierNameMap = ImmutableMap.of(
            "KPMG_SUPPLIER_REPORTER", "KPMG",
            "KORNFERRY_SUPPLIER_REPORTER", "Kornferry",
            "KNOWLEDGEPOOL_SUPPLIER_REPORTER", "Knowledgepool"
    );

    private LearningProviderService learningProviderService;

    @Before
    public void setUp() {
        learningProviderService = new LearningProviderService(authoritySupplierNameMap, learningProviderRepository);
    }

    @Test
    public void shouldReturnLearningProviderName() {

        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("KPMG_SUPPLIER_REPORTER");

        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(grantedAuthority);

        Authentication authentication = mock(Authentication.class);
        doReturn(authorities).when(authentication).getAuthorities();

        assertEquals("KPMG", learningProviderService.getLearningProviderNameFromAuthentication(authentication));
    }

    @Test
    public void shouldThrowInvalidSupplierException() {
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("UNKNOWN_SUPPLIER_REPORTER");
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(grantedAuthority);

        Authentication authentication = mock(Authentication.class);
        doReturn(authorities).when(authentication).getAuthorities();

        try {
            learningProviderService.getLearningProviderNameFromAuthentication(authentication);
            fail("Expected InvalidSupplierException");
        } catch (InvalidSupplierException e) {
            assertEquals("Authority not found in authoritySupplierNameMap: UNKNOWN_SUPPLIER_REPORTER", e.getMessage());
        }
    }

    @Test
    public void shouldReturnLearningProvider() {
        String name = "learning-provider-name";
        LearningProvider learningProvider = new LearningProvider();

        when(learningProviderRepository.findByName(name)).thenReturn(Optional.of(learningProvider));

        assertEquals(learningProvider, learningProviderService.findByName(name));
    }

    @Test
    public void shouldThrowLearningProviderNotFoundException() {
        String name = "learning-provider-name";

        when(learningProviderRepository.findByName(name)).thenReturn(Optional.empty());

        try {
            learningProviderService.findByName(name);
            fail("Expected LearningProviderNotFoundException");
        } catch (LearningProviderNotFoundException e) {
            assertEquals("Learning provider not found: learning-provider-name", e.getMessage());
        }
    }
}