package uk.gov.cslearning.catalogue.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import uk.gov.cslearning.catalogue.service.AuthenticationFacade;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AuditableCourseRepositoryTest {
    @Mock
    private CourseRepository wrappedRepository;

    @InjectMocks
    private AuditableCourseRepository auditableCourseRepository;

    @Test
    public void findMandatoryShouldCallWrappedRepository() {
        String department = "department";
        PageRequest pageRequest = mock(PageRequest.class);

        auditableCourseRepository.findMandatory(department, pageRequest);

        verify(wrappedRepository).findMandatory(department, pageRequest);
    }

    @Test
    public void findSuggestedShouldCallWrappedRepository() {
        String department = "department";
        String areaOfWork = "area-of-work";
        String interest = "interest";

        PageRequest pageRequest = mock(PageRequest.class);

        auditableCourseRepository.findSuggested(department, areaOfWork, interest, pageRequest);

        verify(wrappedRepository).findSuggested(department, areaOfWork, interest, pageRequest);
    }
}