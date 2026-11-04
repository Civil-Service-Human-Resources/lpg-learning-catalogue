package uk.gov.cslearning.catalogue.service.rustici;


import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourse;
import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourseResponse;
import uk.gov.cslearning.catalogue.exception.RusticiEngineException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RusticiEngineClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RusticiEngineClient client;

    @Test
    public void testFailedCreateCourse() {
        CreateCourse createCourse = mock(CreateCourse.class);
        @SuppressWarnings("unchecked")
        ResponseEntity<CreateCourseResponse> response = (ResponseEntity<CreateCourseResponse>) mock(ResponseEntity.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.CONFLICT);
        when(restTemplate.postForEntity(ArgumentMatchers.<String>any(),
                ArgumentMatchers.<RequestEntity<CreateCourse>>any(),
                ArgumentMatchers.<Class<CreateCourseResponse>>any())).thenReturn(response);
        assertThrows(RusticiEngineException.class, () -> client.createCourse(createCourse, "test.test"));
    }

}
