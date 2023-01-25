package uk.gov.cslearning.catalogue.service.rustici;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourse;
import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourseResponse;
import uk.gov.cslearning.catalogue.exception.RusticiEngineException;
import uk.gov.cslearning.catalogue.service.record.RequestEntityException;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class RusticiEngineClient {

    private final RestTemplate restTemplate;

    public RusticiEngineClient(
            @Qualifier("rusticiHttpClient") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private <T> RequestEntity<T> generatePost(String uri, T body) {
        try {
            return new RequestEntity<>(body, HttpMethod.POST, new URI(uri));
        } catch (URISyntaxException e) {
            throw new RequestEntityException(e);
        }
    }

    private <T> RequestEntity<T> generateDelete(String uri) {
        try {
            return new RequestEntity<>(HttpMethod.DELETE, new URI(uri));
        } catch (URISyntaxException e) {
            throw new RequestEntityException(e);
        }
    }

    public CreateCourseResponse createCourse(CreateCourse requestBody, String rusticiCourseId) {
        String uri = String.format("courses?courseId=%s", rusticiCourseId);
        RequestEntity<CreateCourse> req = this.generatePost(uri, requestBody);
        ResponseEntity<CreateCourseResponse> response = restTemplate.exchange(req, CreateCourseResponse.class);
        if (response.getStatusCode().equals(HttpStatus.CONFLICT)) {
            throw new RusticiEngineException(String.format("Error uploading ELearning: course with ID %s already exists in Rustici Engine", rusticiCourseId));
        }
        return response.getBody();
    }

    public void deleteCourse(String rusticiCourseId) {
        String uri = String.format("courses/%s", rusticiCourseId);
        RequestEntity<Void> req = this.generateDelete(uri);
        restTemplate.exchange(req, Void.class);
    }
}
