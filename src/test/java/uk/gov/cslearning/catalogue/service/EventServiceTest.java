package uk.gov.cslearning.catalogue.service;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.Event;
import uk.gov.cslearning.catalogue.domain.module.FaceToFaceModule;
import uk.gov.cslearning.catalogue.domain.module.Module;
import uk.gov.cslearning.catalogue.domain.module.Venue;
import uk.gov.cslearning.catalogue.dto.CourseDto;
import uk.gov.cslearning.catalogue.dto.EventDto;
import uk.gov.cslearning.catalogue.dto.ModuleDto;
import uk.gov.cslearning.catalogue.dto.factory.EventDtoFactory;
import uk.gov.cslearning.catalogue.repository.CourseRepository;
import uk.gov.cslearning.catalogue.service.record.LearnerRecordService;
import uk.gov.cslearning.catalogue.service.record.model.Booking;
import uk.gov.cslearning.catalogue.service.record.model.BookingStatus;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class EventServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private LearnerRecordService learnerRecordService;

    @Mock
    private EventDtoFactory eventDtoFactory;

    @InjectMocks
    private EventService eventService;

    @Test
    public void shouldSaveEvent(){
        String courseId = "courseId";
        String moduleId = "moduleId";

        Event newEvent = new Event();

        Course course = new Course();
        course.setId(courseId);

        Module module = new FaceToFaceModule("product-code");
        module.setId(moduleId);

        Event savedEvent = new Event();
        Collection<Event> events = new HashSet<>();
        events.add(savedEvent);
        ((FaceToFaceModule) module).setEvents(events);

        List<Module> modules = new ArrayList<>();
        modules.add(module);
        course.setModules(modules);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        Assert.assertEquals(eventService.save(courseId, moduleId, newEvent), newEvent);

        verify(courseRepository).findById(courseId);
        verify(courseRepository).save(course);
    }

    @Test
    public void shouldFindEvent(){
        String courseId = "courseId";
        String moduleId = "moduleId";

        Course course = new Course();
        course.setId(courseId);

        Module module = new FaceToFaceModule("product-code");
        module.setId(moduleId);

        Event savedEvent = new Event();
        Venue venue = new Venue();
        venue.setCapacity(10);
        savedEvent.setVenue(venue);

        Collection<Event> events = new HashSet<>();
        events.add(savedEvent);
        ((FaceToFaceModule) module).setEvents(events);

        List<Module> modules = new ArrayList<>();
        modules.add(module);
        course.setModules(modules);

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.CONFIRMED);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(learnerRecordService.getEventBookings(savedEvent.getId())).thenReturn(bookings);

        int availability = (venue.getCapacity() - bookings.size());

        Optional<Event> result = eventService.find(courseId, moduleId, savedEvent.getId());

        Assert.assertEquals(result, Optional.of(savedEvent));
        Assert.assertTrue(result.get().getVenue().getAvailability() == availability);

        verify(courseRepository).findById(courseId);
        verify(learnerRecordService).getEventBookings(savedEvent.getId());
    }

    @Test
    public void shouldGetEventStatus() {
        String eventId = "eventId";

        EventStatus eventStatus = EventStatus.ACTIVE;

        when(learnerRecordService.getEventStatus(eventId)).thenReturn(eventStatus);

        Assert.assertEquals(eventService.getStatus(eventId), eventStatus);

        verify(learnerRecordService).getEventStatus(eventId);
    }

    @Test
    public void shouldGetCancellationReason() {
        String eventId = "eventId";

        CancellationReason cancellationReason = CancellationReason.UNAVAILABLE;

        when(learnerRecordService.getCancellationReason(eventId)).thenReturn(cancellationReason);

        Assert.assertEquals(eventService.getCancellationReason(eventId), cancellationReason);

        verify(learnerRecordService).getCancellationReason(eventId);
    }

    public void shouldReturnMapOfEvents() {
        String eventId = "event-id";
        Event event = new Event();
        event.setId(eventId);

        String moduleId = "module-id";
        String moduleTitle = "module-title";
        String productCode = "product-code";
        FaceToFaceModule module = new FaceToFaceModule(productCode);
        module.setId(moduleId);
        module.setTitle(moduleTitle);
        module.setEvents(Collections.singletonList(event));

        String courseTitle = "course-title";
        String courseId = "course-id";
        Course course = new Course();
        course.setTitle(courseTitle);
        course.setId(courseId);
        course.setModules(Collections.singletonList(module));

        CourseDto courseDto = new CourseDto();
        courseDto.setTitle(courseTitle);
        courseDto.setId(courseId);

        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setId(moduleId);
        moduleDto.setTitle(moduleTitle);

        EventDto eventDto = new EventDto();
        eventDto.setId(eventId);
        eventDto.setModule(moduleDto);
        eventDto.setCourse(courseDto);

        when(courseRepository.findEvents()).thenReturn(Collections.singletonList(course));
        when(eventDtoFactory.create(event, module, course)).thenReturn(eventDto);

        Map<String, EventDto> eventDtoMap = ImmutableMap.of(eventId, eventDto);

        assertEquals(eventDtoMap, eventService.getEventMap());
    }
}
