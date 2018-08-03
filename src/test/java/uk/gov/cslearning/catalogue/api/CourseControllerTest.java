package uk.gov.cslearning.catalogue.api;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.BlogModule;
import uk.gov.cslearning.catalogue.domain.module.Module;
import uk.gov.cslearning.catalogue.repository.CourseRepository;
import uk.gov.cslearning.catalogue.repository.ResourceRepository;
import uk.gov.cslearning.catalogue.service.ModuleService;

import java.net.URL;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(CourseController.class)
@WithMockUser(username = "user", password = "password")
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    private ResourceRepository resourceRepository;

    @MockBean
    private ModuleService moduleService;

    private Gson gson = new Gson();

    @Test
    public void shouldReturnNotFoundForUnknownCourse() throws Exception {
        mockMvc.perform(
                get("/courses/abc")
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
                get("/courses/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", equalTo("title")));
    }

    @Test
    public void shouldCreateCourseAndRedirectToNewResource() throws Exception {
        final String newId = "newId";

        Course course = createCourse();

        when(courseRepository.save(any()))
                .thenAnswer((Answer<Course>) invocation -> {
                    course.setId(newId);
                    return course;
                });

        mockMvc.perform(
                post("/courses").with(csrf())
                        .content(gson.toJson(course))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "http://localhost/courses/" + newId));
    }

    @Test
    public void shouldUpdateExistingCourse() throws Exception {
        Course course = createCourse();
        when(courseRepository.existsById(course.getId())).thenReturn(true);
        when(courseRepository.save(any())).thenReturn(course);

        mockMvc.perform(
                put("/courses/" + course.getId()).with(csrf())
                        .content(gson.toJson(course))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnBadRequestIfUpdatedCourseDoesntExist() throws Exception {
        Course course = createCourse();
        when(courseRepository.existsById(course.getId())).thenReturn(false);

        mockMvc.perform(
                put("/courses/" + course.getId()).with(csrf())
                        .content(gson.toJson(course))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldCreateModule() throws Exception {
        String moduleId = "module-id";
        Module module = mock(BlogModule.class);
        when(module.getId()).thenReturn(moduleId);


        String courseId = UUID.randomUUID().toString();
        String json = gson.toJson(ImmutableMap.of("type", "link", "location", "http://localhost"));

        when(moduleService.save(eq(courseId), any(Module.class))).thenReturn(module);

        mockMvc.perform(
                post(String.format("/courses/%s/modules/", courseId)).with(csrf())
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", String.format("http://localhost/courses/%s/modules/%s", courseId, moduleId)));
    }

    @Test
    public void shouldReturnBadRequestIfCourseNotFoundWhenSavingModule() throws Exception {
        String moduleId = "module-id";
        Module module = mock(BlogModule.class);
        when(module.getId()).thenReturn(moduleId);

        String courseId = "course-id";

        String json = gson.toJson(ImmutableMap.of("type", "link", "location", "http://localhost"));

        IllegalStateException exception = mock(IllegalStateException.class);

        doThrow(exception).when(moduleService).save(eq(courseId), any(Module.class));

        mockMvc.perform(
                post(String.format("/courses/%s/modules/", courseId)).with(csrf())
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFindModule() throws Exception {
        String courseId = "course-id";
        String moduleId = "module-id";
        String location = "http://example.org";

        Module module = new BlogModule(new URL(location));

        when(moduleService.find(courseId, moduleId)).thenReturn(Optional.of(module));

        mockMvc.perform(
                get(String.format("/courses/%s/modules/%s", courseId, moduleId)).with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location", equalTo(location)));
    }

    @Test
    public void shouldReturnBadRequestIfCourseNotFoundWhenFindingModule() throws Exception {
        String courseId = "course-id";
        String moduleId = "module-id";

        IllegalStateException exception = mock(IllegalStateException.class);

        doThrow(exception).when(moduleService).find(courseId, moduleId);

        mockMvc.perform(
                get(String.format("/courses/%s/modules/%s", courseId, moduleId)).with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnNotFoundIfModuleNotFound() throws Exception {
        String courseId = "course-id";
        String moduleId = "module-id";

        when(moduleService.find(courseId, moduleId)).thenReturn(Optional.empty());

        mockMvc.perform(
                get(String.format("/courses/%s/modules/%s", courseId, moduleId)).with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private Course createCourse() {
        return new Course("title", "shortDescription", "description",
                "learningOutcomes");
    }
}
