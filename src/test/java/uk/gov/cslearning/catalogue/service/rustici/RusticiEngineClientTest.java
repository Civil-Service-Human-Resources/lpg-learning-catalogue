package uk.gov.cslearning.catalogue.service.rustici;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourse;
import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourseResponse;
import uk.gov.cslearning.catalogue.exception.RusticiEngineException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RusticiEngineClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RusticiEngineClient client;

    @Test(expected = RusticiEngineException.class)
    public void testFailedCreateCourse() {
        CreateCourse createCourse = mock(CreateCourse.class);
        ResponseEntity<CreateCourseResponse> response = mock(ResponseEntity.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.CONFLICT);
        when(restTemplate.postForEntity(ArgumentMatchers.<String>any(),
                ArgumentMatchers.<RequestEntity<CreateCourse>>any(),
                ArgumentMatchers.<Class<CreateCourseResponse>>any())).thenReturn(response);
        client.createCourse(createCourse, "test.test");
    }

}
