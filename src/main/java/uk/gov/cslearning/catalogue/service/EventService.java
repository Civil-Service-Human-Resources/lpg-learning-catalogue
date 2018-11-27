package uk.gov.cslearning.catalogue.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.Event;
import uk.gov.cslearning.catalogue.domain.module.FaceToFaceModule;
import uk.gov.cslearning.catalogue.repository.CourseRepository;
import uk.gov.cslearning.catalogue.service.record.LearnerRecordService;
import uk.gov.cslearning.catalogue.service.record.model.Booking;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class EventService {
    private final CourseRepository courseRepository;

    private final LearnerRecordService learnerRecordService;

    public EventService(CourseRepository courseRepository, LearnerRecordService learnerRecordService){
        this.courseRepository = courseRepository;
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

            List<Booking> bookings = learnerRecordService.getEventBookings(eventId);

            event.getVenue().setAvailability(event.getVenue().getCapacity());
            bookings.forEach(b -> {
                if (b.getStatus() != "Cancelled") {
                    event.getVenue().setAvailability(event.getVenue().getAvailability() - 1);
                }
            });
        }

        return result;
    }
}
