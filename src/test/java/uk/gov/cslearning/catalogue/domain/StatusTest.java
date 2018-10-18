package uk.gov.cslearning.catalogue.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class StatusTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void shouldSerializeJsonWithDefaultDraftStatus() throws IOException {
        Course course = new Course();
        String json = objectMapper.writeValueAsString(course);
        assertEquals("Draft", JsonPath.read(json, "$.status"));
    }

    @Test
    public void shouldSerializeJsonWithPublishedStatus() throws IOException {
        Course course = new Course();
        course.setStatus(Status.PUBLISHED);
        String json = objectMapper.writeValueAsString(course);
        assertEquals("Published", JsonPath.read(json, "$.status"));
    }

    @Test
    public void shouldSerializeJsonWithArchivedStatus() throws IOException {
        Course course = new Course();
        course.setStatus(Status.ARCHIVED);
        String json = objectMapper.writeValueAsString(course);
        assertEquals("Archived", JsonPath.read(json, "$.status"));
    }

    @Test
    public void shouldDeserializeJson() throws IOException {
        assertEquals(Status.DRAFT, objectMapper.readValue("{ \"status\": \"Draft\" }", Course.class).getStatus());
        assertEquals(Status.PUBLISHED, objectMapper.readValue("{ \"status\": \"Published\" }", Course.class).getStatus());
        assertEquals(Status.ARCHIVED, objectMapper.readValue("{ \"status\": \"Archived\" }", Course.class).getStatus());
    }
}