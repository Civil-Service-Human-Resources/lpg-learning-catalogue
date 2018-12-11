package uk.gov.cslearning.catalogue.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.CancellationReason;
import uk.gov.cslearning.catalogue.domain.module.Event;
import uk.gov.cslearning.catalogue.domain.module.EventStatus;
import uk.gov.cslearning.catalogue.domain.module.FaceToFaceModule;
import uk.gov.cslearning.catalogue.domain.module.Module;
import uk.gov.cslearning.catalogue.dto.EventDto;
import uk.gov.cslearning.catalogue.dto.factory.EventDtoFactory;
import uk.gov.cslearning.catalogue.repository.CourseRepository;
import uk.gov.cslearning.catalogue.service.record.LearnerRecordService;
import uk.gov.cslearning.catalogue.service.record.model.Booking;
import uk.gov.cslearning.catalogue.service.record.model.BookingStatus;

import java.util.*;
import java.util.function.Supplier;

@Service
public class EventService {
    private final CourseRepository courseRepository;
    private final EventDtoFactory eventDtoFactory;
    private final LearnerRecordService learnerRecordService;

    public EventService(CourseRepository courseRepository, EventDtoFactory eventDtoFactory, LearnerRecordService learnerRecordService) {
        this.courseRepository = courseRepository;
        this.eventDtoFactory = eventDtoFactory;
        this.learnerRecordService = learnerRecordService;
    }

    public Event save(String courseId, String moduleId, Event event){
        Course course = courseRepository.findById(courseId).orElseThrow((Supplier<IllegalStateException>) () -> {
            throw new IllegalStateException(
                    String.format("Unable to add event. Course does not exist: %s", courseId));
        });

        FaceToFaceModule module = (FaceToFaceModule) course.getModuleById(moduleId);

        if(module == null){
            throw new IllegalStateException(
                    String.format("Unable to add event. Module does not exist: %s", moduleId));
        }

        Collection<Event> events = module.getEvents();

        HashSet<Event> newEvents = new HashSet<>();

        for(Event e : events){
            newEvents.add(e);
        }

        newEvents.add(event);

        module.setEvents(newEvents);
        courseRepository.save(course);

        return event;
    }

    public Optional<Event> find(String courseId, String moduleId, String eventId){
        Course course = courseRepository.findById(courseId).orElseThrow((Supplier<IllegalStateException>) () -> {
            throw new IllegalStateException(
                    String.format("Unable to find event: %s. Course does not exist: %s", eventId, courseId));
        });

        FaceToFaceModule module = (FaceToFaceModule) course.getModuleById(moduleId);

        if(module == null){
            throw new IllegalStateException(
                    String.format("Unable to find event: %s. Module does not exist: %s", eventId, moduleId));
        }

        Optional<Event> result = module.getEvents().stream().filter(e -> e.getId().equals(eventId)).findFirst();

        if(result.isPresent()){
            Event event = result.get();
            event = getEventAvailability(event);
            event.setStatus(getStatus(eventId));

            if(event.getStatus() == EventStatus.CANCELLED){
                event.setCancellationReason(getCancellationReason(eventId));
            }
        }

        return result;
    }

    public Event getEventAvailability(Event event) {
        List<Booking> bookings = learnerRecordService.getEventBookings(event.getId());

        event.getVenue().setAvailability(event.getVenue().getCapacity());

        bookings.forEach(b -> {
            if (b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.REQUESTED) {
                event.getVenue().setAvailability(event.getVenue().getAvailability() - 1);
            }
        });

        return event;
    }

    public EventStatus getStatus(String eventId){
        return learnerRecordService.getEventStatus(eventId);
    }

    public CancellationReason getCancellationReason(String eventId) {
        return learnerRecordService.getCancellationReason(eventId);

    public Map<String, EventDto> getEventMap() {
        Map<String, EventDto> results = new HashMap<>();

        List<Course> courses = courseRepository.findEvents();

        for (Course course : courses) {
            for (Module module : course.getModules()) {
                for (Event event : ((FaceToFaceModule) module).getEvents()) {
                    results.put(event.getId(), eventDtoFactory.create(event, (FaceToFaceModule) module, course));
                }
            }
        }

        return results;
    }
}
