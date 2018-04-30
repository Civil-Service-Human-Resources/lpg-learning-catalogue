package uk.gov.cslearning.catalogue.api;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.repository.CourseRepository;
import uk.gov.cslearning.catalogue.repository.ResourceRepository;

import java.util.Optional;

import static java.util.Collections.emptySet;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class CourseControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private CourseController controller;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ResourceRepository resourceRepository;


    @Before
    public void setup() {
        initMocks(this);
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void shouldReturnNotFoundForUnknownCourse() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/courses/abc")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnCourse() throws Exception {

        Course course = createCourse();

        when(courseRepository.findById("1"))
                .thenReturn(Optional.of(course));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/courses/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", equalTo("title")));
    }

    @Test
    public void shouldCreateCourseAndRedirectToNewResource() throws Exception {

        Gson gson = new Gson();

        final String newId = "newId";

        Course course = createCourse();

        when(courseRepository.save(any()))
                .thenAnswer((Answer<Course>) invocation -> {
                    course.setId(newId);
                    return course;
                });

        mockMvc.perform(
                MockMvcRequestBuilders.post("/courses")
                        .content(gson.toJson(course))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "http://localhost/courses/" + newId));
    }

    @Test
    public void shouldUpdateExistingCourse() throws Exception {

        Gson gson = new Gson();

        Course course = createCourse();
        when(courseRepository.existsById(course.getId())).thenReturn(true);
        when(courseRepository.save(any())).thenReturn(course);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/courses/" + course.getId())
                        .content(gson.toJson(course))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnBadRequestIfUpdatedCourseDoesntExist() throws Exception {

        Gson gson = new Gson();

        Course course = createCourse();
        when(courseRepository.existsById(course.getId())).thenReturn(false);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/courses/" + course.getId())
                        .content(gson.toJson(course))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    private Course createCourse() {
        return new Course("title", "shortDescription", "description",
                "learningOutcomes");
    }
}
