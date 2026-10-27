package uk.gov.cslearning.catalogue.integration;

import org.json.JSONObject;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import uk.gov.cslearning.catalogue.domain.CivilServant.CivilServant;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.Status;
import uk.gov.cslearning.catalogue.domain.Visibility;
import uk.gov.cslearning.catalogue.domain.learningTag.LearningTagPartialDto;
import uk.gov.cslearning.catalogue.domain.module.*;
import uk.gov.cslearning.catalogue.service.RegistryService;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CourseControllerTest extends IntegrationTestBase {

    @MockitoBean
    private RegistryService mockRegistryService;

    @BeforeEach
    public void before() {
        // Any endpoint annotated with @PreAuthorize will call CsrsService to get the current civil servant.
        // We should mock this call out with wiremock but until then let's just mock the method out
        when(mockRegistryService.getCurrentCivilServant()).thenReturn(new CivilServant());
    }


    @Test
    @Order(1)
    public void testGetCourses() throws Exception {

        mvc.perform(get("/courses")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].title").value("Required course 1"))
                .andExpect(jsonPath("$.results[0].shortDescription").value("Required course 1 short description"))
                .andExpect(jsonPath("$.results[0].description").value("Required course 1 long description"))
                .andExpect(jsonPath("$.results[0].status").value("Published"))
                .andExpect(jsonPath("$.results[1].title").value("Required course 2"))
                .andExpect(jsonPath("$.results[1].shortDescription").value("Required course 2 short description"))
                .andExpect(jsonPath("$.results[1].description").value("Required course 2 long description"))
                .andExpect(jsonPath("$.results[1].status").value("Published"));
    }

    @Test
    @Order(2)
    public void testGetCoursesWithPagination() throws Exception {

        mvc.perform(get("/courses?size=1&page=0")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResults").value(13))
                .andExpect(jsonPath("$.results[0].title").value("Required course 1"))
                .andExpect(jsonPath("$.results[0].shortDescription").value("Required course 1 short description"))
                .andExpect(jsonPath("$.results[0].description").value("Required course 1 long description"))
                .andExpect(jsonPath("$.results[0].status").value("Published"));

        mvc.perform(get("/courses?size=1&page=1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].title").value("Required course 2"))
                .andExpect(jsonPath("$.results[0].shortDescription").value("Required course 2 short description"))
                .andExpect(jsonPath("$.results[0].description").value("Required course 2 long description"))
                .andExpect(jsonPath("$.results[0].status").value("Published"));

    }

    @Test
    @Order(2)
    public void testGetMandatoryLearning() throws Exception {

        mvc.perform(get("/courses?mandatory=true&days=7&size=1000000000")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.HMRC[0].title").value("Required course 2"))
                .andExpect(jsonPath("$.HMRC[0].shortDescription").value("Required course 2 short description"))
                .andExpect(jsonPath("$.HMRC[0].description").value("Required course 2 long description"))
                .andExpect(jsonPath("$.HMRC[0].status").value("Published"));

    }

    @Test
    @Order(3)
    @WithMockUser(value = "spring", authorities = {"CSL_AUTHOR"})
    public void testCreateModule() throws Exception {

        Course tempCourse = dataService.createCourse("CreateModule");
        dataService.repository().save(tempCourse);

        String json = new JSONObject()
                .put("url", "https://url.com")
                .put("title", "Link module")
                .put("description", "A link module")
                .put("duration", 100)
                .put("cost", 0)
                .put("optional", false)
                .put("associatedLearning", false)
                .put("type", "link")
                .toString();

        mvc.perform(post(String.format("/courses/%s/modules", tempCourse.getId()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mvc.perform(get(String.format("/courses/%s", tempCourse.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modules[0].title").value("Link module"))
                .andExpect(jsonPath("$.modules[0].description").value("A link module"))
                .andExpect(jsonPath("$.modules[0].cost").value(0))
                .andExpect(jsonPath("$.modules[0].duration").value(100))
                .andExpect(jsonPath("$.modules[0].moduleType").value("link"))
                .andExpect(jsonPath("$.modules[0].type").value("link"))
                .andExpect(jsonPath("$.modules[0].optional").value(false));

        dataService.repository().delete(tempCourse);

    }

    @Test
    @Order(4)
    @WithMockUser(value = "spring", authorities = {"LEARNING_UNARCHIVE"})
    public void testUnarchiveCourse() throws Exception {
        Course tempCourse = dataService.createCourse("archiveCourse");
        tempCourse.setStatus(Status.ARCHIVED);
        dataService.repository().save(tempCourse);

        Course update = dataService.repository().findById(tempCourse.getId()).get();
        update.setStatus(Status.DRAFT);
        mvc.perform(put(String.format("/courses/%s", tempCourse.getId()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isOk());
        assertEquals(Status.DRAFT, dataService.repository().findById(tempCourse.getId()).get().getStatus());
    }

    @Test
    @Order(5)
    @WithMockUser(value = "spring", authorities = {"LEARNING_PUBLISH"})
    public void testUnarchiveCourseIncorrectPermission() throws Exception {
        Course tempCourse = dataService.createCourse("archiveCourse");
        tempCourse.setStatus(Status.ARCHIVED);
        dataService.repository().save(tempCourse);

        Course update = dataService.repository().findById(tempCourse.getId()).get();
        update.setStatus(Status.DRAFT);
        mvc.perform(put(String.format("/courses/%s", tempCourse.getId()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(6)
    @WithMockUser(value = "spring", authorities = {"LEARNING_EDIT"})
    public void testOrderModules() throws Exception {
        Course tempCourse = dataService.createCourse("orderModules");
        ELearningModule m1 = dataService.createELearningModule();
        m1.setId("FIRST");
        ELearningModule m2 = dataService.createELearningModule();
        m2.setId("SECOND");
        tempCourse.setModules(List.of(m1, m2));
        dataService.repository().save(tempCourse);

        mvc.perform(put(String.format("/courses/%s/modules", tempCourse.getId()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"startPage\":\"http://startPage\",\"url\":\"https://url.com\",\"id\":\"SECOND\",\"title\":\"ELearning module\",\"description\":\"An ELearning module\",\"duration\":100,\"cost\":0.0,\"optional\":false,\"status\":null,\"associatedLearning\":false,\"createdTimestamp\":null,\"updatedTimestamp\":null,\"mediaId\":null,\"moduleType\":\"elearning\",\"type\":\"elearning\"},{\"startPage\":\"http://startPage\",\"url\":\"https://url.com\",\"id\":\"FIRST\",\"title\":\"ELearning module\",\"description\":\"An ELearning module\",\"duration\":100,\"cost\":0.0,\"optional\":false,\"status\":null,\"associatedLearning\":false,\"createdTimestamp\":null,\"updatedTimestamp\":null,\"mediaId\":null,\"moduleType\":\"elearning\",\"type\":\"elearning\"}]"))
                .andExpect(status().isOk());
        Course result = dataService.repository().findById(tempCourse.getId()).get();
        assertEquals("SECOND", result.getModules().get(0).getId());
        assertEquals("FIRST", result.getModules().get(1).getId());
    }

    @Test
    @Order(7)
    @WithMockUser(value = "spring", authorities = {"CSL_AUTHOR"})
    public void testUpdatingCourseToStatusPublishedSetsHasBeenPublishedToTrue() throws Exception {
        String courseId = "test-course-01";

        Course course = new Course();
        course.setId(courseId);
        course.setTitle("My course");
        course.setStatus(Status.DRAFT);
        course.setHasBeenPublished(false);

        dataService.repository().save(course);

        String json = new JSONObject()
                .put("id", courseId)
                .put("title", "My course")
                .put("status", "Published")
                .toString();

        mvc.perform(put(String.format("/courses/%s", course.getId()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());


        mvc.perform(get(String.format("/courses/%s", course.getId()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Published"))
                .andExpect(jsonPath("$.hasBeenPublished").value(true));
    }

    @Test
    @Order(8)
    @WithMockUser(value = "spring", authorities = {"CSL_AUTHOR"})
    public void testUpdatingCourseToStatusDraftDoesNotChangeHasBeenPublishedValueFromTrue() throws Exception {
        String courseId = "test-course-02";

        Course course = new Course();
        course.setId(courseId);
        course.setTitle("My course 02");
        course.setStatus(Status.PUBLISHED);
        course.setHasBeenPublished(true);

        dataService.repository().save(course);

        String json = new JSONObject()
                .put("id", courseId)
                .put("title", "My course")
                .put("status", "Draft")
                .toString();

        mvc.perform(put(String.format("/courses/%s", course.getId()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());


        mvc.perform(get(String.format("/courses/%s", course.getId()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Draft"))
                .andExpect(jsonPath("$.hasBeenPublished").value(true));
    }

    @Test
    @Order(9)
    @WithMockUser(value = "spring", authorities = {"CSL_AUTHOR"})
    public void testDeletingCourseRemovesDocumentIfCourseIsDraftAndNotPublished() throws Exception {
        Course course = new Course();
        course.setTitle("My course 03");
        course.setStatus(Status.DRAFT);
        course.setHasBeenPublished(false);

        dataService.repository().save(course);

        mvc.perform(delete(String.format("/courses/%s", course.getId()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        mvc.perform(get(String.format("/courses/%s", course.getId()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(10)
    @WithMockUser(value = "spring", authorities = {"CSL_AUTHOR"})
    public void testDeletingCourseDoesNotRemovesDocumentIfCourseIsDraftAndPublishedAndThrowsConflictError() throws Exception {
        String courseId = "test-course-04";

        Course course = new Course();
        course.setId(courseId);
        course.setTitle("My course 04");
        course.setStatus(Status.DRAFT);
        course.setHasBeenPublished(true);

        dataService.repository().save(course);

        mvc.perform(delete(String.format("/courses/%s", course.getId()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());


        mvc.perform(get(String.format("/courses/%s", course.getId()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(11)
    @WithMockUser(value = "spring", authorities = {"CSL_AUTHOR"})
    public void testCreateCourseWithAllAttributes() throws Exception {
        String courseId = "test-course-all-attributes";

        // Course
        Course course = createCourse(courseId);

        // LearningTag
        LearningTagPartialDto tag = new LearningTagPartialDto(101L, "Digital Skills", "digital-skills");
        course.setLearningTags(List.of(tag));

        // Audience
        Audience audience = createAudience();
        course.setAudiences(Set.of(audience));

        // Link module
        LinkModule linkModule = createLinkModule();

        // ELearning module
        ELearningModule eLearningModule = createELearningModule();

        // Video module
        VideoModule videoModule = createVideoModule();

        // File module
        FileModule fileModule = createFileModule();

        // Face to Face module
        FaceToFaceModule f2fModule = createF2fModule();

        // Event
        Event event = createEvent();

        // Venue
        Venue venue = createVenue();

        event.setVenue(venue);

        DateRange dateRange = new DateRange(
                LocalDate.of(2026, 11, 20),
                LocalTime.of(9, 30),
                LocalTime.of(16, 30)
        );
        event.setDateRanges(List.of(dateRange));
        f2fModule.setEvents(List.of(event));

        course.setModules(List.of(linkModule, eLearningModule, videoModule, fileModule, f2fModule));

        String courseJson = mapper.writeValueAsString(course);

        mvc.perform(post("/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(courseJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/courses/" + courseId));

        mvc.perform(get(String.format("/courses/%s", courseId))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(courseId))
                .andExpect(jsonPath("$.title").value("Complete Integration Test Course"))
                .andExpect(jsonPath("$.shortDescription").value("A comprehensive course short description"))
                .andExpect(jsonPath("$.description").value("A detailed and comprehensive long description of the course covering all attributes."))
                .andExpect(jsonPath("$.learningOutcomes").value("Understand all required and optional attributes of a course."))
                .andExpect(jsonPath("$.preparation").value("Complete prerequisite setup before attending."))
                .andExpect(jsonPath("$.visibility").value("PUBLIC"))
                .andExpect(jsonPath("$.status").value("Published"))
                .andExpect(jsonPath("$.topicId").value("topic-technology-001"))
                .andExpect(jsonPath("$.cost").value(150.00))
                .andExpect(jsonPath("$.hasBeenPublished").value(true))
                .andExpect(jsonPath("$.learningTags[0].id").value(101))
                .andExpect(jsonPath("$.learningTags[0].name").value("Digital Skills"))
                .andExpect(jsonPath("$.learningTags[0].urlSlug").value("digital-skills"))
                .andExpect(jsonPath("$.audiences[0].id").value("audience-001"))
                .andExpect(jsonPath("$.audiences[0].name").value("Digital Delivery Audience"))
                .andExpect(jsonPath("$.audiences[0].type").value("REQUIRED_LEARNING"))
                .andExpect(jsonPath("$.audiences[0].frequency").value("YEARLY"))
                .andExpect(jsonPath("$.audiences[0].eventId").value("event-001"))
                .andExpect(jsonPath("$.modules[0].id").value("mod-link-001"))
                .andExpect(jsonPath("$.modules[0].type").value("link"))
                .andExpect(jsonPath("$.modules[0].moduleType").value("link"))
                .andExpect(jsonPath("$.modules[0].url").value("https://example.com/link-resource"))
                .andExpect(jsonPath("$.modules[1].id").value("mod-elearning-001"))
                .andExpect(jsonPath("$.modules[1].type").value("elearning"))
                .andExpect(jsonPath("$.modules[1].moduleType").value("elearning"))
                .andExpect(jsonPath("$.modules[1].startPage").value("https://example.com/elearning/start.html"))
                .andExpect(jsonPath("$.modules[1].url").value("https://example.com/elearning/content"))
                .andExpect(jsonPath("$.modules[1].mediaId").value("media-elearning-123"))
                .andExpect(jsonPath("$.modules[2].id").value("mod-video-001"))
                .andExpect(jsonPath("$.modules[2].type").value("video"))
                .andExpect(jsonPath("$.modules[2].moduleType").value("video"))
                .andExpect(jsonPath("$.modules[2].url").value("https://example.com/video/lecture.mp4"))
                .andExpect(jsonPath("$.modules[3].id").value("mod-file-001"))
                .andExpect(jsonPath("$.modules[3].type").value("file"))
                .andExpect(jsonPath("$.modules[3].moduleType").value("file"))
                .andExpect(jsonPath("$.modules[3].url").value("https://example.com/files/guide.pdf"))
                .andExpect(jsonPath("$.modules[3].fileSize").value(2048000))
                .andExpect(jsonPath("$.modules[3].mediaId").value("media-file-123"))
                .andExpect(jsonPath("$.modules[4].id").value("mod-f2f-001"))
                .andExpect(jsonPath("$.modules[4].type").value("face-to-face"))
                .andExpect(jsonPath("$.modules[4].moduleType").value("face-to-face"))
                .andExpect(jsonPath("$.modules[4].productCode").value("PROD-F2F-001"))
                .andExpect(jsonPath("$.modules[4].events[0].id").value("event-001"))
                .andExpect(jsonPath("$.modules[4].events[0].joiningInstructions").value("Please arrive 15 minutes prior to the session with your laptop."))
                .andExpect(jsonPath("$.modules[4].events[0].status").value("Active"))
                .andExpect(jsonPath("$.modules[4].events[0].cancellationReason").value("short notice unavailability of the venue"))
                .andExpect(jsonPath("$.modules[4].events[0].venue.location").value("London HQ"))
                .andExpect(jsonPath("$.modules[4].events[0].venue.address").value("1 Horse Guards Road, London, SW1A 2HQ"))
                .andExpect(jsonPath("$.modules[4].events[0].venue.capacity").value(30))
                .andExpect(jsonPath("$.modules[4].events[0].venue.minCapacity").value(5))
                .andExpect(jsonPath("$.modules[4].events[0].venue.availability").value(25))
                .andExpect(jsonPath("$.modules[4].events[0].dateRanges[0].date").value("2026-11-20"));

        dataService.repository().delete(course);
    }

    private static @NonNull Course createCourse(String courseId) {
        Course course = new Course();
        course.setId(courseId);
        course.setTitle("Complete Integration Test Course");
        course.setShortDescription("A comprehensive course short description");
        course.setDescription("A detailed and comprehensive long description of the course covering all attributes.");
        course.setLearningOutcomes("Understand all required and optional attributes of a course.");
        course.setPreparation("Complete prerequisite setup before attending.");
        course.setVisibility(Visibility.PUBLIC);
        course.setStatus(Status.PUBLISHED);
        course.setTopicId("topic-technology-001");
        course.setCost(BigDecimal.valueOf(150.00));
        course.setHasBeenPublished(true);
        return course;
    }

    private static @NonNull Venue createVenue() {
        Venue venue = new Venue();
        venue.setLocation("London HQ");
        venue.setAddress("1 Horse Guards Road, London, SW1A 2HQ");
        venue.setCapacity(30);
        venue.setMinCapacity(5);
        venue.setAvailability(25);
        return venue;
    }

    private static @NonNull Event createEvent() {
        Event event = new Event();
        event.setId("event-001");
        event.setJoiningInstructions("Please arrive 15 minutes prior to the session with your laptop.");
        event.setStatus(EventStatus.ACTIVE);
        event.setCancellationReason(CancellationReason.VENUE);
        return event;
    }

    private static @NonNull FaceToFaceModule createF2fModule() {
        FaceToFaceModule f2fModule = new FaceToFaceModule("PROD-F2F-001");
        f2fModule.setId("mod-f2f-001");
        f2fModule.setTitle("Face to Face Module");
        f2fModule.setDescription("Description for face to face module");
        f2fModule.setDuration(7200L);
        f2fModule.setCost(BigDecimal.valueOf(150.00));
        f2fModule.setOptional(false);
        f2fModule.setStatus(Status.PUBLISHED);
        f2fModule.setAssociatedLearning(false);
        return f2fModule;
    }

    private static @NonNull VideoModule createVideoModule() throws MalformedURLException {
        VideoModule videoModule = new VideoModule(URI.create("https://example.com/video/lecture.mp4").toURL());
        videoModule.setId("mod-video-001");
        videoModule.setTitle("Video Module");
        videoModule.setDescription("Description for video module");
        videoModule.setDuration(1200L);
        videoModule.setCost(BigDecimal.ZERO);
        videoModule.setOptional(true);
        videoModule.setStatus(Status.PUBLISHED);
        videoModule.setAssociatedLearning(true);
        return videoModule;
    }

    private static @NonNull LinkModule createLinkModule() throws MalformedURLException {
        LinkModule linkModule = new LinkModule(URI.create("https://example.com/link-resource").toURL());
        linkModule.setId("mod-link-001");
        linkModule.setTitle("Link Module");
        linkModule.setDescription("Description for link module");
        linkModule.setDuration(1800L);
        linkModule.setCost(BigDecimal.ZERO);
        linkModule.setOptional(false);
        linkModule.setStatus(Status.PUBLISHED);
        linkModule.setAssociatedLearning(false);
        return linkModule;
    }

    private static @NonNull FileModule createFileModule() {
        FileModule fileModule = new FileModule("https://example.com/files/guide.pdf", 2048000L);
        fileModule.setId("mod-file-001");
        fileModule.setTitle("File Module");
        fileModule.setDescription("Description for file module");
        fileModule.setDuration(900L);
        fileModule.setCost(BigDecimal.ZERO);
        fileModule.setOptional(false);
        fileModule.setStatus(Status.PUBLISHED);
        fileModule.setAssociatedLearning(false);
        fileModule.setMediaId("media-file-123");
        return fileModule;
    }

    private static @NonNull ELearningModule createELearningModule() {
        ELearningModule eLearningModule = new ELearningModule("https://example.com/elearning/start.html", "https://example.com/elearning/content");
        eLearningModule.setId("mod-elearning-001");
        eLearningModule.setTitle("ELearning Module");
        eLearningModule.setDescription("Description for elearning module");
        eLearningModule.setDuration(3600L);
        eLearningModule.setCost(BigDecimal.ZERO);
        eLearningModule.setOptional(false);
        eLearningModule.setStatus(Status.PUBLISHED);
        eLearningModule.setAssociatedLearning(false);
        eLearningModule.setMediaId("media-elearning-123");
        return eLearningModule;
    }

    private static @NonNull Audience createAudience() {
        Audience audience = new Audience();
        audience.setId("audience-001");
        audience.setName("Digital Delivery Audience");
        audience.setType(Audience.Type.REQUIRED_LEARNING);
        audience.setDepartments(Set.of("CO", "HMRC"));
        audience.setAreasOfWork(Set.of("Digital", "Commercial"));
        audience.setInterests(Set.of("Leadership", "Software Engineering"));
        audience.setGrades(Set.of("G6", "G7", "SCS"));
        audience.setRequiredBy(Instant.parse("2026-12-31T00:00:00Z"));
        audience.setFrequency("YEARLY");
        audience.setEventId("event-001");
        return audience;
    }
}
