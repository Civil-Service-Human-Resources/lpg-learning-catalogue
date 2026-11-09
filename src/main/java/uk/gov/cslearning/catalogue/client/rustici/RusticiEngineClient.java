package uk.gov.cslearning.catalogue.client.rustici;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.catalogue.client.IHttpClient;
import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourse;
import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourseResponse;

import java.net.URI;

@Service
public class RusticiEngineClient implements IRusticiEngineClient {

    private final IHttpClient httpClient;

    public RusticiEngineClient(@Qualifier("rusticiHttpClient") IHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public CreateCourseResponse createCourse(CreateCourse requestBody, String rusticiCourseId) {
        URI uri = UriComponentsBuilder.fromPath("/courses")
                .queryParam("courseId", rusticiCourseId)
                .build()
                .toUri();
        RequestEntity<CreateCourse> requestEntity = RequestEntity.post(uri).body(requestBody);
        return httpClient.executeRequest(requestEntity, CreateCourseResponse.class);
    }

    public void deleteCourse(String rusticiCourseId) {
        URI uri = UriComponentsBuilder.fromPath("/courses/{courseId}")
                .buildAndExpand(rusticiCourseId)
                .toUri();
        RequestEntity<Void> requestEntity = RequestEntity.delete(uri).build();
        httpClient.executeRequest(requestEntity, Void.class);
    }
}
