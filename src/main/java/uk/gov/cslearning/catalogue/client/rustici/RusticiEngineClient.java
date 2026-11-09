package uk.gov.cslearning.catalogue.client.rustici;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.client.IHttpClient;
import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourse;
import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourseResponse;

@Service
public class RusticiEngineClient implements IRusticiEngineClient {

    private final IHttpClient httpClient;

    public RusticiEngineClient(@Qualifier("rusticiHttpClient") IHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public CreateCourseResponse createCourse(CreateCourse requestBody, String rusticiCourseId) {
        RequestEntity<CreateCourse> requestEntity = RequestEntity.post("/courses?courseId={courseId}", rusticiCourseId)
                .body(requestBody);
        return httpClient.executeRequest(requestEntity, CreateCourseResponse.class);
    }

    public void deleteCourse(String rusticiCourseId) {
        RequestEntity<Void> requestEntity = RequestEntity.delete("/courses/{courseId}", rusticiCourseId).build();
        httpClient.executeRequest(requestEntity, Void.class);
    }
}
