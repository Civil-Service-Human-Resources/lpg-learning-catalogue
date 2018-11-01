package uk.gov.cslearning.catalogue.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.Status;
import uk.gov.cslearning.catalogue.domain.Visibility;
import uk.gov.cslearning.catalogue.domain.module.Audience;
import uk.gov.cslearning.catalogue.domain.module.DateRange;
import uk.gov.cslearning.catalogue.domain.module.Event;
import uk.gov.cslearning.catalogue.domain.module.FaceToFaceModule;
import uk.gov.cslearning.catalogue.domain.module.LinkModule;
import uk.gov.cslearning.catalogue.domain.module.Module;
import uk.gov.cslearning.catalogue.domain.module.Venue;
import uk.gov.cslearning.catalogue.repository.CourseRepository;
import uk.gov.cslearning.catalogue.repository.ResourceRepository;
import uk.gov.cslearning.catalogue.service.EventService;
import uk.gov.cslearning.catalogue.service.ModuleService;
import uk.gov.cslearning.catalogue.service.upload.AudienceService;

import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.cslearning.catalogue.exception.ResourceNotFoundException.resourceNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(CourseController.class)
@WithMockUser(username = "user")
@EnableSpringDataWebSupport
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

    @MockBean
    private AudienceService audienceService;

    private ObjectMapper objectMapper;


    @Before
    public void setUp() throws Exception {

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    public void shouldReturnNotFoundForUnknownCourse() throws Exception {
        mockMvc.perform(
                get("/courses/abc")
                        .accept(MediaType.APPLICATION_JSON))
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
                        .content(objectMapper.writeValueAsString(course))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
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
                        .content(objectMapper.writeValueAsString(course))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnBadRequestIfUpdatedCourseDoesntExist() throws Exception {
        Course course = createCourse();
        when(courseRepository.existsById(course.getId())).thenReturn(false);

        mockMvc.perform(
                put("/courses/" + course.getId()).with(csrf())
                        .content(objectMapper.writeValueAsString(course))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldCreateModule() throws Exception {
        String moduleId = "module-id";
        Module module = mock(LinkModule.class);
        when(module.getId()).thenReturn(moduleId);

        String courseId = UUID.randomUUID().toString();
        String json = objectMapper.writeValueAsString(ImmutableMap.of("type", "link", "location", "http://localhost"));

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

        String json = objectMapper.writeValueAsString(ImmutableMap.of("type", "link", "location", "http://localhost"));

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
    public void shouldCreateAudience() throws Exception {
        String audienceId = "audience-id";
        Audience audience = mock(Audience.class);
        when(audience.getId()).thenReturn(audienceId);

        String courseId = UUID.randomUUID().toString();

        mockMvc.perform(
                post(String.format("/courses/%s/audiences/", courseId)).with(csrf())
                        .content(objectMapper.writeValueAsString(ImmutableMap.of("id", audienceId, "name", "Audience name")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", String.format("http://localhost/courses/%s/audiences/%s", courseId, audienceId)));
    }

    @Test
    public void shouldFindAudience() throws Exception {
        String courseId = "course-id";
        String audienceId = "audience-id";

        Audience audience = new Audience();
        audience.setId(audienceId);

        when(courseRepository.existsById(courseId)).thenReturn(true);
        when(audienceService.find(courseId, audienceId)).thenReturn(Optional.of(audience));

        mockMvc.perform(
                get(String.format("/courses/%s/audiences/%s", courseId, audienceId)).with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(audienceId)));
    }

    @Test
    public void shouldReturnNotFoundIfAudienceNotFound() throws Exception {
        String courseId = "course-id";
        String audienceId = "audience-id";

        when(courseRepository.existsById(courseId)).thenReturn(true);
        when(audienceService.find(courseId, audienceId)).thenReturn(Optional.empty());

        mockMvc.perform(
                get(String.format("/courses/%s/audiences/%s", courseId, audienceId)).with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnNotFoundIfCourseNotFoundWhenFindingAudience() throws Exception {
        String courseId = "course-id";
        String audienceId = "audience-id";

        doThrow(resourceNotFoundException()).when(audienceService).find(courseId, audienceId);

        mockMvc.perform(
                get(String.format("/courses/%s/audiences/%s", courseId, audienceId)).with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldDeleteAudience() throws Exception {
        Course course = new Course();
        Audience audience = new Audience();
        Set<Audience> audiences = new HashSet<>();
        audiences.add(audience);
        course.setAudiences(audiences);

        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));
        when(audienceService.find(course, audience.getId())).thenReturn(Optional.of(audience));
        when(courseRepository.save(course)).thenReturn(course);

        assertThat(course.getAudiences().isEmpty(), is(not(true)));

        mockMvc.perform(
                delete(String.format("/courses/%s/audiences/%s", course.getId(), audience.getId())).with(csrf()))
                .andExpect(status().isNoContent());

        assertThat(course.getAudiences().isEmpty(), is(true));
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
                        .content(objectMapper.writeValueAsString(event))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldReturnEvent() throws Exception {
        String courseId = "course-id";
        String moduleId = "module-id";
        String eventId = "event-id";

        Event event = new Event();

        when(courseRepository.existsById(courseId)).thenReturn(true);
        when(eventService.find(courseId, moduleId, eventId)).thenReturn(Optional.of(event));

        mockMvc.perform(
                get(String.format("/courses/%s/modules/%s/events/%s", courseId, moduleId, eventId)).with(csrf())
                        .content(objectMapper.writeValueAsString(event))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
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
        DateRange dateRange = new DateRange();
        dateRange.setStartDateTime(Instant.now());
        dateRange.setEndDateTime(Instant.now().plus(1, ChronoUnit.HOURS));

        List<DateRange> dateRanges = Collections.singletonList(dateRange);
        Venue venue = new Venue();
        venue.setLocation("venue-location");
        venue.setAddress("venue-address");
        venue.setCapacity(10);
        venue.setMinCapacity(5);

        Course course = new Course();

        Event oldEvent = new Event();
        Event newEvent = new Event();

        newEvent.setJoiningInstructions("new");
        newEvent.setDateRanges(dateRanges);
        newEvent.setVenue(venue);
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
                        .content(objectMapper.writeValueAsString(newEvent))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Event savedEvent = module.getEvents().stream().filter(e -> e.getId().equals(oldEvent.getId())).findFirst().get();

        assert (module.getEvents().size() == 1);
        assertEquals(savedEvent.getId(), oldEvent.getId());
        assertEquals("new", savedEvent.getJoiningInstructions());
        assertEquals(venue, savedEvent.getVenue());
        assertEquals(dateRange, savedEvent.getDateRanges().get(0));
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
                delete(String.format("/courses/%s/modules/%s/events/%s", course.getId(), module.getId(), event.getId())).with(csrf()))
                .andExpect(status().isNoContent());

        assert (module.getEvents().isEmpty());
    }

    @Test
    public void shouldFindSuggestedCourses() throws Exception {
        String areaOfWork = "area-of-work";
        String department = "department";
        String interest = "_interest";
        String status = "status";

        Course course = new Course();

        when(courseRepository.findSuggested(eq(department), eq(areaOfWork), eq(interest), eq(status), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(course)));

        mockMvc.perform(
                get("/courses/")
                        .param("areaOfWork", areaOfWork)
                        .param("department", department)
                        .param("interest", interest)
                        .param("status", status)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id", equalTo(course.getId())));
    }

    @Test
    public void shouldDefaultToShowingAllPublicCourses() throws Exception {
        Course course = new Course();

        when(courseRepository.findAllByStatusIn(eq(Collections.singletonList(Status.PUBLISHED)), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(course)));

        mockMvc.perform(
                get("/courses/")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id", equalTo(course.getId())));
    }

    @Test
    public void shouldFindMultipleStatuses() throws Exception {
        Course course = new Course();

        when(courseRepository.findAllByStatusIn(eq(Arrays.asList(Status.DRAFT, Status.PUBLISHED, Status.ARCHIVED)), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(course)));

        mockMvc.perform(
                get("/courses/")
                        .param("status", "Draft", "Published", "Archived")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id", equalTo(course.getId())));
    }

    @Test
    public void shouldDefaultMissingParametersToNone() throws Exception {
        String areaOfWork = "none";
        String department = "none";
        String interest = "_interest";
        String status = "Published";

        Course course = new Course();

        when(courseRepository.findSuggested(eq(department), eq(areaOfWork), eq(interest), eq(status), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(course)));

        mockMvc.perform(
                get("/courses/")
                        .param("interest", interest)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id", equalTo(course.getId())));
    }

    @Test
    public void shouldDefaultMissingInterestParameterToNone() throws Exception {
        String areaOfWork = "area-of-work";
        String department = "none";
        String interest = "none";
        String status = "Published";

        Course course = new Course();

        when(courseRepository.findSuggested(eq(department), eq(areaOfWork), eq(interest), eq(status), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(course)));

        mockMvc.perform(
                get("/courses/")
                        .param("areaOfWork", areaOfWork)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id", equalTo(course.getId())));
    }


    @Test
    public void shouldConcatenateMultipleParameters() throws Exception {
        String areaOfWork = "area-of-work1,area-of-work2";
        String department = "department1,department2";
        String interest = "interest1,interest2";
        String status = "Published";

        Course course = new Course();

        when(courseRepository.findSuggested(eq(department), eq(areaOfWork), eq(interest), eq(status), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(course)));

        mockMvc.perform(
                get("/courses/")
                        .param("areaOfWork", "area-of-work1", "area-of-work2")
                        .param("department", "department1", "department2")
                        .param("interest", "interest1", "interest2")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id", equalTo(course.getId())));
    }

    @Test
    public void shouldListMandatoryCourses() throws Exception {
        String department = "department1";
        String status = "Published";

        Course course = new Course();

        when(courseRepository.findMandatory(eq(department), eq(status), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(course)));

        mockMvc.perform(
                get("/courses/")
                        .param("department", "department1")
                        .param("mandatory", "true")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id", equalTo(course.getId())));
    }

    @Test
    public void shouldListMandatoryCoursesWithMultipleParameters() throws Exception {
        String department = "department1,department2";
        String status = "Draft,Published";

        Course course = new Course();

        when(courseRepository.findMandatory(eq(department), eq(status), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(course)));

        mockMvc.perform(
                get("/courses/")
                        .param("department", "department1", "department2")
                        .param("status", "Draft", "Published")
                        .param("mandatory", "true")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id", equalTo(course.getId())));

    }

    private Course createCourse() {
        return new Course("title", "shortDescription", "description",
                Visibility.PUBLIC);
    }
}
