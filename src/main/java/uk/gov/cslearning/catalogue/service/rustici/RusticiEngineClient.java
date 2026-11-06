package uk.gov.cslearning.catalogue.service.rustici;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.client.IHttpClient;
import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourse;
import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourseResponse;

@Service
@Slf4j
public class RusticiEngineClient {

    private final IHttpClient httpClient;

    public RusticiEngineClient(
            @Qualifier("rusticiHttpClient") IHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public CreateCourseResponse createCourse(CreateCourse requestBody, String rusticiCourseId) {
        String url = String.format("/courses?courseId=%s", rusticiCourseId);
        RequestEntity<CreateCourse> requestEntity = RequestEntity.post(url).body(requestBody);
        return httpClient.executeRequest(requestEntity, CreateCourseResponse.class);
    }

    public void deleteCourse(String rusticiCourseId) {
        String url = String.format("/courses/%s", rusticiCourseId);
        RequestEntity<Void> requestEntity = RequestEntity.delete(url).build();
        httpClient.executeRequest(requestEntity, Void.class);
    }
}
