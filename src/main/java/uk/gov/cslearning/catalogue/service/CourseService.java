package uk.gov.cslearning.catalogue.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.FaceToFaceModule;
import uk.gov.cslearning.catalogue.repository.CourseRepository;

import java.util.Optional;

@Service
public class CourseService {
    private final CourseRepository courseRepository;

    private final EventService eventService;

    public CourseService(CourseRepository courseRepository, EventService eventService) {
        this.courseRepository = courseRepository;
        this.eventService = eventService;
    }

    public Optional<Course> findById(String courseId) {
        Optional<Course> result = courseRepository.findById(courseId);

        if(result.isPresent()){
            getCourseEventsAvailability(result.get());
        }

        return result;
    }

    private Course getCourseEventsAvailability(Course course) {
        course.getModules().forEach(module -> {
            if(module instanceof FaceToFaceModule) {
                ((FaceToFaceModule) module).getEvents().forEach(event -> eventService.getEventAvailability(event));
            }
        });

        return course;
    }
}
