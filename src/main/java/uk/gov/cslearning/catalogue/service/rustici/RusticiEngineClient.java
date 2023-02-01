package uk.gov.cslearning.catalogue.service.rustici;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourse;
import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourseResponse;
import uk.gov.cslearning.catalogue.exception.RusticiEngineException;

@Service
public class RusticiEngineClient {

    private final RestTemplate restTemplate;

    public RusticiEngineClient(
            @Qualifier("rusticiHttpClient") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CreateCourseResponse createCourse(CreateCourse requestBody, String rusticiCourseId) {
        String url = String.format("/courses?courseId=%s", rusticiCourseId);
        ResponseEntity<CreateCourseResponse> response = restTemplate.postForEntity(url, requestBody, CreateCourseResponse.class);
        if (response.getStatusCode().equals(HttpStatus.CONFLICT)) {
            throw new RusticiEngineException(String.format("Error uploading ELearning: course with ID %s already exists in Rustici Engine", rusticiCourseId));
        }
        return response.getBody();
    }

    public void deleteCourse(String rusticiCourseId) {
        String url = String.format("/courses/%s", rusticiCourseId);
        restTemplate.delete(url);
    }
}
