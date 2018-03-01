package uk.gov.cslearning.catalogue.api;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.repository.CourseRepository;

import java.util.Optional;

import static java.util.Collections.emptySet;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class CourseControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private CourseController controller;

    @Mock
    private CourseRepository courseRepository;

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

        Course course = new Course("title", "shortDescription", "description",
                "learningOutcomes", 1000, emptySet());

        when(courseRepository.findById("1"))
                .thenReturn(Optional.of(course));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/courses/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", equalTo("title")));
    }
}
