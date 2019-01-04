package uk.gov.cslearning.catalogue.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.*;
import uk.gov.cslearning.catalogue.repository.CourseRepository;
import uk.gov.cslearning.catalogue.service.record.LearnerRecordService;
import uk.gov.cslearning.catalogue.service.record.model.Booking;
import uk.gov.cslearning.catalogue.service.record.model.BookingStatus;


import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceTest {
    @Mock
    private CourseRepository courseRepository;

    @Mock
    private LearnerRecordService learnerRecordService;

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
}
