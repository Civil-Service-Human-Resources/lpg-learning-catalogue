package uk.gov.cslearning.catalogue.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.Event;
import uk.gov.cslearning.catalogue.domain.module.FaceToFaceModule;
import uk.gov.cslearning.catalogue.domain.module.Module;
import uk.gov.cslearning.catalogue.repository.CourseRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EventService eventService;

    @InjectMocks
    private CourseService courseService;

    @Test
    public void shouldFindCourseAndGetEventAvailabilities() {
        String courseId = "courseId";

        Course course = new Course();
        FaceToFaceModule module = new FaceToFaceModule("product code");
        Event event = new Event();

        Collection<Event> events = new ArrayList<>();
        events.add(event);
        module.setEvents(events);

        List<Module> modules = new ArrayList<>();
        modules.add(module);
        course.setModules(modules);

        Mockito.when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        Mockito.when(eventService.getEventAvailability(event)).thenReturn(event);

        Assert.assertEquals(courseService.findById(courseId), Optional.of(course));

        Mockito.verify(courseRepository).findById(courseId);
        Mockito.verify(eventService).getEventAvailability(event);
    }

}
