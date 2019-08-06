package uk.gov.cslearning.catalogue.service;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.DateRange;
import uk.gov.cslearning.catalogue.domain.module.Event;
import uk.gov.cslearning.catalogue.domain.module.FaceToFaceModule;
import uk.gov.cslearning.catalogue.domain.module.LinkModule;
import uk.gov.cslearning.catalogue.dto.CourseDto;
import uk.gov.cslearning.catalogue.dto.EventDto;
import uk.gov.cslearning.catalogue.dto.ModuleDto;
import uk.gov.cslearning.catalogue.dto.factory.EventDtoFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EventDtoMapServiceTest {

    private final long retentionTimeInDays = 90;

    @Mock
    private EventDateService eventDateService;

    @Mock
    private EventDtoFactory eventDtoFactory;

    private EventDtoMapService eventDtoMapService;

    @Before
    public void setUp() {
        eventDtoMapService = new EventDtoMapService(eventDateService, eventDtoFactory, retentionTimeInDays);
    }

    @Test
    public void shouldReturnStringEventDtoMap() throws MalformedURLException {
        String eventId = "event-id";
        Event event = new Event();
        event.setId(eventId);

        String moduleId = "module-id";
        String moduleTitle = "module-title";
        String productCode = "product-code";

        FaceToFaceModule faceToFaceModule = new FaceToFaceModule(productCode);
        faceToFaceModule.setId(moduleId);
        faceToFaceModule.setTitle(moduleTitle);
        faceToFaceModule.setEvents(Collections.singletonList(event));

        LinkModule linkModule = new LinkModule(URI.create("http://example.org").toURL());

        String courseTitle = "course-title";
        String courseId = "course-id";
        Course course = new Course();
        course.setTitle(courseTitle);
        course.setId(courseId);
        course.setModules(Arrays.asList(faceToFaceModule, linkModule));

        List<Course> courseList = Arrays.asList(course);

        CourseDto courseDto = new CourseDto();
        courseDto.setTitle(courseTitle);
        courseDto.setId(courseId);

        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setId(moduleId);
        moduleDto.setTitle(moduleTitle);
        moduleDto.setCourse(courseDto);

        EventDto eventDto = new EventDto();
        eventDto.setId(eventId);
        eventDto.setModule(moduleDto);

        Map<String, EventDto> eventDtoMap = ImmutableMap.of(eventId, eventDto);

        when(eventDtoFactory.create(event, faceToFaceModule, course)).thenReturn(eventDto);

        assertEquals(eventDtoMap, eventDtoMapService.getStringEventDtoMap(courseList));
        assertEquals(1, eventDtoMapService.getStringEventDtoMap(courseList).size());
    }

    @Test
    public void shouldNotReturnMapIfNoEvents() throws MalformedURLException {
        String eventId = "event-id";

        Event event = new Event();

        String moduleId = "module-id";
        String moduleTitle = "module-title";
        String productCode = "product-code";

        FaceToFaceModule faceToFaceModule = new FaceToFaceModule(productCode);
        faceToFaceModule.setId(moduleId);
        faceToFaceModule.setTitle(moduleTitle);

        LinkModule linkModule = new LinkModule(URI.create("http://example.org").toURL());

        String courseTitle = "course-title";
        String courseId = "course-id";
        Course course = new Course();
        course.setTitle(courseTitle);
        course.setId(courseId);
        course.setModules(Arrays.asList(faceToFaceModule, linkModule));

        List<Course> courseList = Arrays.asList(course);

        CourseDto courseDto = new CourseDto();
        courseDto.setTitle(courseTitle);
        courseDto.setId(courseId);

        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setId(moduleId);
        moduleDto.setTitle(moduleTitle);
        moduleDto.setCourse(courseDto);

        EventDto eventDto = new EventDto();
        eventDto.setId(eventId);
        eventDto.setModule(moduleDto);

        verify(eventDtoFactory, times(0)).create(event, faceToFaceModule, course);
        assertEquals(0, eventDtoMapService.getStringEventDtoMap(courseList).size());
    }

    @Test
    public void shouldNotReturnMapIfNoFaceToFaceModules() throws MalformedURLException {
        String eventId = "event-id";

        Event event = new Event();

        String moduleId = "module-id";
        String moduleTitle = "module-title";
        String productCode = "product-code";

        FaceToFaceModule faceToFaceModule = new FaceToFaceModule(productCode);
        faceToFaceModule.setId(moduleId);
        faceToFaceModule.setTitle(moduleTitle);

        LinkModule linkModule = new LinkModule(URI.create("http://example.org").toURL());

        String courseTitle = "course-title";
        String courseId = "course-id";
        Course course = new Course();
        course.setTitle(courseTitle);
        course.setId(courseId);
        course.setModules(Arrays.asList(linkModule));

        List<Course> courseList = Arrays.asList(course);

        CourseDto courseDto = new CourseDto();
        courseDto.setTitle(courseTitle);
        courseDto.setId(courseId);

        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setId(moduleId);
        moduleDto.setTitle(moduleTitle);
        moduleDto.setCourse(courseDto);

        EventDto eventDto = new EventDto();
        eventDto.setId(eventId);
        eventDto.setModule(moduleDto);

        verify(eventDtoFactory, times(0)).create(event, faceToFaceModule, course);
        assertEquals(0, eventDtoMapService.getStringEventDtoMap(courseList).size());
    }

    @Test
    public void shouldNotReturnDtoMapForSupplierIfOutsideRetentionTime() throws MalformedURLException {
        String eventId = "event-id";

        DateRange dateRange1 = new DateRange();
        dateRange1.setDate(LocalDate.now().minusDays(120));

        Event event = new Event();
        event.setId(eventId);

        String moduleId = "module-id";
        String moduleTitle = "module-title";
        String productCode = "product-code";

        FaceToFaceModule faceToFaceModule = new FaceToFaceModule(productCode);
        faceToFaceModule.setId(moduleId);
        faceToFaceModule.setTitle(moduleTitle);
        faceToFaceModule.setEvents(Collections.singletonList(event));

        LinkModule linkModule = new LinkModule(URI.create("http://example.org").toURL());

        String courseTitle = "course-title";
        String courseId = "course-id";
        Course course = new Course();
        course.setTitle(courseTitle);
        course.setId(courseId);
        course.setModules(Arrays.asList(faceToFaceModule, linkModule));

        List<Course> courseList = Arrays.asList(course);

        CourseDto courseDto = new CourseDto();
        courseDto.setTitle(courseTitle);
        courseDto.setId(courseId);

        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setId(moduleId);
        moduleDto.setTitle(moduleTitle);
        moduleDto.setCourse(courseDto);

        EventDto eventDto = new EventDto();
        eventDto.setId(eventId);
        eventDto.setModule(moduleDto);

        Optional<DateRange> optionalDateRange = Optional.of(dateRange1);
        when(eventDateService.getFirstDateChronologically(event)).thenReturn(optionalDateRange);

        verify(eventDtoFactory, times(0)).create(event, faceToFaceModule, course);
        assertEquals(0, eventDtoMapService.getStringEventDtoMapForSupplier(courseList).size());
    }

    @Test
    public void shouldReturnDtoMapForSupplierIfLessThanRetentionTime() throws MalformedURLException {
        String eventId = "event-id";

        LocalDate todayMinus120Days = LocalDate.now().minusDays(10);
        LocalDate todayMinus90Days = LocalDate.now().minusDays(89);

        DateRange dateRange1 = new DateRange();
        dateRange1.setDate(todayMinus120Days);
        DateRange dateRange2 = new DateRange();
        dateRange2.setDate(todayMinus90Days);

        List<DateRange> dateRanges = Arrays.asList(dateRange1, dateRange2);

        Event event = new Event();
        event.setDateRanges(dateRanges);
        event.setId(eventId);

        String moduleId = "module-id";
        String moduleTitle = "module-title";
        String productCode = "product-code";

        FaceToFaceModule faceToFaceModule = new FaceToFaceModule(productCode);
        faceToFaceModule.setId(moduleId);
        faceToFaceModule.setTitle(moduleTitle);
        faceToFaceModule.setEvents(Collections.singletonList(event));

        LinkModule linkModule = new LinkModule(URI.create("http://example.org").toURL());

        String courseTitle = "course-title";
        String courseId = "course-id";
        Course course = new Course();
        course.setTitle(courseTitle);
        course.setId(courseId);
        course.setModules(Arrays.asList(faceToFaceModule, linkModule));

        List<Course> courseList = Arrays.asList(course);

        CourseDto courseDto = new CourseDto();
        courseDto.setTitle(courseTitle);
        courseDto.setId(courseId);

        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setId(moduleId);
        moduleDto.setTitle(moduleTitle);
        moduleDto.setCourse(courseDto);

        EventDto eventDto = new EventDto();
        eventDto.setId(eventId);
        eventDto.setModule(moduleDto);

        Optional<DateRange> optionalDateRange = Optional.of(dateRange2);
        when(eventDateService.getFirstDateChronologically(event)).thenReturn(optionalDateRange);

        when(eventDtoFactory.create(event, faceToFaceModule, course)).thenReturn(eventDto);

        assertEquals(1, eventDtoMapService.getStringEventDtoMapForSupplier(courseList).size());
    }

    @Test
    public void shouldNotReturnSupplierMapIfNoEvents() throws MalformedURLException {
        String eventId = "event-id";

        Event event = new Event();

        String moduleId = "module-id";
        String moduleTitle = "module-title";
        String productCode = "product-code";

        FaceToFaceModule faceToFaceModule = new FaceToFaceModule(productCode);
        faceToFaceModule.setId(moduleId);
        faceToFaceModule.setTitle(moduleTitle);

        LinkModule linkModule = new LinkModule(URI.create("http://example.org").toURL());

        String courseTitle = "course-title";
        String courseId = "course-id";
        Course course = new Course();
        course.setTitle(courseTitle);
        course.setId(courseId);
        course.setModules(Arrays.asList(faceToFaceModule, linkModule));

        List<Course> courseList = Arrays.asList(course);

        CourseDto courseDto = new CourseDto();
        courseDto.setTitle(courseTitle);
        courseDto.setId(courseId);

        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setId(moduleId);
        moduleDto.setTitle(moduleTitle);
        moduleDto.setCourse(courseDto);

        EventDto eventDto = new EventDto();
        eventDto.setId(eventId);
        eventDto.setModule(moduleDto);

        verify(eventDtoFactory, times(0)).create(event, faceToFaceModule, course);
        assertEquals(0, eventDtoMapService.getStringEventDtoMapForSupplier(courseList).size());
    }

    @Test
    public void shouldNotReturnSupplierMapIfNoFaceToFaceModules() throws MalformedURLException {
        String eventId = "event-id";

        Event event = new Event();

        String moduleId = "module-id";
        String moduleTitle = "module-title";
        String productCode = "product-code";

        FaceToFaceModule faceToFaceModule = new FaceToFaceModule(productCode);
        faceToFaceModule.setId(moduleId);
        faceToFaceModule.setTitle(moduleTitle);

        LinkModule linkModule = new LinkModule(URI.create("http://example.org").toURL());

        String courseTitle = "course-title";
        String courseId = "course-id";
        Course course = new Course();
        course.setTitle(courseTitle);
        course.setId(courseId);
        course.setModules(Arrays.asList(linkModule));

        List<Course> courseList = Arrays.asList(course);

        CourseDto courseDto = new CourseDto();
        courseDto.setTitle(courseTitle);
        courseDto.setId(courseId);

        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setId(moduleId);
        moduleDto.setTitle(moduleTitle);
        moduleDto.setCourse(courseDto);

        EventDto eventDto = new EventDto();
        eventDto.setId(eventId);
        eventDto.setModule(moduleDto);

        verify(eventDtoFactory, times(0)).create(event, faceToFaceModule, course);
        assertEquals(0, eventDtoMapService.getStringEventDtoMapForSupplier(courseList).size());
    }
}
