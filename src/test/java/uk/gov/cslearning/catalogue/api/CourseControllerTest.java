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
import uk.gov.cslearning.catalogue.domain.Visibility;
import uk.gov.cslearning.catalogue.domain.module.*;
import uk.gov.cslearning.catalogue.repository.CourseRepository;
import uk.gov.cslearning.catalogue.repository.ResourceRepository;
import uk.gov.cslearning.catalogue.service.EventService;
import uk.gov.cslearning.catalogue.service.ModuleService;

import java.net.URL;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @MockBean
    private EventService eventService;

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
        Module module = mock(LinkModule.class);
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
                .andExpect(header().string("Location", String.format("http://localhost/courses/%s/modules/%s", courseId, moduleId)));
    }

    @Test
    public void shouldReturnBadRequestIfCourseNotFoundWhenSavingModule() throws Exception {
        String moduleId = "module-id";
        Module module = mock(LinkModule.class);
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
        String url = "http://example.org";

        Module module = new LinkModule(new URL(url));

        when(courseRepository.existsById(courseId)).thenReturn(true);
        when(moduleService.find(courseId, moduleId)).thenReturn(Optional.of(module));

        mockMvc.perform(
                get(String.format("/courses/%s/modules/%s", courseId, moduleId)).with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url", equalTo(url)));
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

        when(courseRepository.existsById(courseId)).thenReturn(true);
        when(moduleService.find(courseId, moduleId)).thenReturn(Optional.empty());

        mockMvc.perform(
                get(String.format("/courses/%s/modules/%s", courseId, moduleId)).with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldAddEventToModule() throws Exception {
        Event event = new Event();
        event.setJoiningInstructions("");
        event.setDateRanges(new ArrayList<>());
        event.setVenue(new Venue("location"));

        String courseId = "course-id";
        String moduleId = "module-id";

        when(courseRepository.existsById(courseId)).thenReturn(true);

        when(eventService.save(eq(courseId), eq(moduleId), any(Event.class))).thenReturn(event);

        mockMvc.perform(
                post(String.format("/courses/%s/modules/%s/events", courseId, moduleId)).with(csrf())
                        .content(gson.toJson(event))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldReturnEvent() throws Exception{
        String courseId = "course-id";
        String moduleId = "module-id";
        String eventId = "event-id";

        Event event = new Event();

        when(courseRepository.existsById(courseId)).thenReturn(true);
        when(eventService.find(courseId, moduleId, eventId)).thenReturn(Optional.of(event));

        mockMvc.perform(
                get(String.format("/courses/%s/modules/%s/events/%s", courseId, moduleId, eventId)).with(csrf())
                        .content(gson.toJson(event))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnNotFoundIfEventNotFound() throws Exception {
        String courseId = "course-id";
        String moduleId = "module-id";
        String eventId = "event-id";

        when(eventService.find(courseId, moduleId, eventId)).thenReturn(Optional.empty());

        mockMvc.perform(
                get(String.format("/courses/%s/modules/%s/events/%s", courseId, moduleId, eventId)).with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldUpdateEvent() throws Exception {
        Course course = new Course();

        Event oldEvent = new Event();
        Event newEvent = new Event();

        newEvent.setJoiningInstructions("new");
        oldEvent.setJoiningInstructions("old");

        FaceToFaceModule module = new FaceToFaceModule("product-code");

        HashSet<Event> events = new HashSet<>();
        events.add(oldEvent);
        module.setEvents(events);

        List<Module> modules = new ArrayList<>();
        modules.add(module);
        course.setModules(modules);

        when(courseRepository.existsById(course.getId())).thenReturn(true);

        Optional<Course> result = Optional.of(course);
        when(courseRepository.findById(course.getId())).thenReturn(result);

        when(courseRepository.save(course)).thenReturn(course);

        mockMvc.perform(
                put(String.format("/courses/%s/modules/%s/events/%s", course.getId(), module.getId(), oldEvent.getId())).with(csrf())
                        .content(gson.toJson(newEvent))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Event savedEvent = module.getEvents().stream().filter(e -> e.getId().equals(oldEvent.getId())).findFirst().get();

        assert(module.getEvents().size() == 1);
        assertEquals(savedEvent.getId(), oldEvent.getId());
        assertEquals("new", savedEvent.getJoiningInstructions());
    }

    @Test
    public void shouldDeleteEvent() throws Exception {
        Course course = new Course();
        FaceToFaceModule module = new FaceToFaceModule("product-code");
        Event event = new Event();

        HashSet<Event> events = new HashSet<>();
        events.add(event);
        module.setEvents(events);

        List<Module> modules = new ArrayList<>();
        modules.add(module);
        course.setModules(modules);

        when(courseRepository.existsById(course.getId())).thenReturn(true);

        Optional<Course> result = Optional.of(course);
        when(courseRepository.findById(course.getId())).thenReturn(result);

        when(courseRepository.save(course)).thenReturn(course);

        mockMvc.perform(
                delete(String.format("/courses/%s/modules/%s/events/%s", course.getId(), module.getId(), event.getId())).with(csrf())
                        .content(gson.toJson(event))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        assert(module.getEvents().isEmpty());
    }

    private Course createCourse() {
        return new Course("title", "shortDescription", "description",
                Visibility.PUBLIC);
    }
}
