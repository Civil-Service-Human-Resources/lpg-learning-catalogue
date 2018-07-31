package uk.gov.cslearning.catalogue.service;

import org.elasticsearch.ResourceNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.LinkModule;
import uk.gov.cslearning.catalogue.domain.module.Module;
import uk.gov.cslearning.catalogue.repository.CourseRepository;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ModuleServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private ModuleService moduleService;

    @Test
    public void shouldSaveModuleToCourse() throws Exception {
        String courseId = "course-id";
        Module module = new LinkModule(new URI("http://localhost").toURL());
        Course course = new Course();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        assertEquals(module, moduleService.save(courseId, module));
        assertEquals(Collections.singletonList(module), course.getModules());
        verify(courseRepository).save(course);
    }

    @Test
    public void shouldThrowExceptionIfCourseNotFound() throws Exception {
        String courseId = "course-id";
        Module module = new LinkModule(new URI("http://localhost").toURL());

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        try {
            moduleService.save(courseId, module);
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals("Unable to add module. Course does not exist: " + courseId, e.getMessage());
        }
    }

    @Test
    public void shouldReturnModule() throws Exception {
        String courseId = "course-id";

        Module module1 = new LinkModule(new URI("http://module1").toURL());
        Module module2 = new LinkModule(new URI("http://module2").toURL());
        Module module3 = new LinkModule(new URI("http://module3").toURL());

        Course course = new Course();
        course.setModules(Arrays.asList(module1, module2, module3));

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        Optional<Module> result = moduleService.find(courseId, module2.getId());

        assertTrue(result.isPresent());
        assertEquals(module2, result.get());
    }

    @Test
    public void shouldReturnEmptyOptional() throws Exception {
        String courseId = "course-id";
        String moduleId = "module-id";

        Module module1 = new LinkModule(new URI("http://module1").toURL());
        Module module2 = new LinkModule(new URI("http://module2").toURL());
        Module module3 = new LinkModule(new URI("http://module3").toURL());

        Course course = new Course();
        course.setModules(Arrays.asList(module1, module2, module3));

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        Optional<Module> result = moduleService.find(courseId, moduleId);

        assertFalse(result.isPresent());
    }

    @Test
    public void shouldThrowExceptionIfCourseForModuleNotFound() {
        String courseId = "course-id";
        String moduleId = "module-id";

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        try {
            moduleService.find(courseId, moduleId);
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals(String.format("Unable to find module: %s. Course does not exist: %s", moduleId, courseId), e.getMessage());
        }
    }

}