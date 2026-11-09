package uk.gov.cslearning.catalogue.service.rustici;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.RequestEntity;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.catalogue.client.IHttpClient;
import uk.gov.cslearning.catalogue.client.rustici.RusticiEngineClient;
import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourse;
import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourseResponse;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RusticiEngineClientTest {

    @Mock
    private IHttpClient httpClient;

    @InjectMocks
    private RusticiEngineClient client;

    @Test
    public void testCreateCourse() {
        CreateCourse createCourse = mock(CreateCourse.class);
        CreateCourseResponse response = mock(CreateCourseResponse.class);
        String rusticiCourseId = "test.test";
        URI uri = UriComponentsBuilder.fromPath("/courses")
                .queryParam("courseId", rusticiCourseId)
                .build()
                .toUri();
        RequestEntity<CreateCourse> requestEntity = RequestEntity.post(uri).body(createCourse);

        when(httpClient.executeRequest(requestEntity, CreateCourseResponse.class)).thenReturn(response);

        CreateCourseResponse result = client.createCourse(createCourse, rusticiCourseId);

        assertEquals(response, result);
    }

    @Test
    public void testDeleteCourse() {
        String rusticiCourseId = "test.test";
        URI uri = UriComponentsBuilder.fromPath("/courses/{courseId}")
                .buildAndExpand(rusticiCourseId)
                .toUri();
        RequestEntity<Void> requestEntity = RequestEntity.delete(uri).build();

        client.deleteCourse(rusticiCourseId);

        verify(httpClient).executeRequest(requestEntity, Void.class);
    }
}
