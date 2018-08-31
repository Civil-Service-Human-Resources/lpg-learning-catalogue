package uk.gov.cslearning.catalogue.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.Event;
import uk.gov.cslearning.catalogue.domain.module.Module;
import uk.gov.cslearning.catalogue.repository.CourseRepository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class EventService {
    private final CourseRepository courseRepository;

    public EventService(CourseRepository courseRepository){
        this.courseRepository = courseRepository;
    }

    public Event save(String courseId, String moduleId, Event event){
        Course course = courseRepository.findById(courseId).orElseThrow((Supplier<IllegalStateException>) () -> {
            throw new IllegalStateException(
                    String.format("Unable to add module. Course does not exist: %s", courseId));
        });

        Module module = course.getModuleById(moduleId);

        ArrayList<Event> events = module.getEvents();
        events.add(event);
        module.setEvents(events);
        courseRepository.save(course);

        return event;
    }

    public Optional<Event> find(String courseId, String moduleId, String eventId){
        Course course = courseRepository.findById(courseId).orElseThrow((Supplier<IllegalStateException>) () -> {
            throw new IllegalStateException(
                    String.format("Unable to find module: %s. Course does not exist: %s", moduleId, courseId));
        });

        Module module = course.getModuleById(moduleId);

        return module.getEvents().stream().filter(e -> e.getId().equals(eventId)).findFirst();
    }
}
