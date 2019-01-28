package uk.gov.cslearning.catalogue.service;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.FaceToFaceModule;
import uk.gov.cslearning.catalogue.domain.module.FileModule;
import uk.gov.cslearning.catalogue.domain.module.LinkModule;
import uk.gov.cslearning.catalogue.domain.module.Module;
import uk.gov.cslearning.catalogue.dto.ModuleDto;
import uk.gov.cslearning.catalogue.dto.factory.ModuleDtoFactory;
import uk.gov.cslearning.catalogue.repository.CourseRepository;
import uk.gov.cslearning.catalogue.service.upload.FileUploadService;

import java.net.URI;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ModuleServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private ModuleDtoFactory moduleDtoFactory;

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

    @Test
    public void shouldUpdateModule() throws Exception {
        String moduleId = "moduleId";
        String courseId = "course-id";
        String url = "https://www.example.com";
        String updatedTitle = "title-updated";
        Course course = new Course();
        Module module = new LinkModule(new URL(url));
        module.setId(moduleId);
        module.setTitle("title");
        List<Module> modules = new ArrayList<>();
        modules.add(module);
        course.setModules(modules);
        Module newModule = new LinkModule(new URL(url));
        newModule.setId(moduleId);
        newModule.setTitle(updatedTitle);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        moduleService.updateModule(courseId, newModule);
        assertEquals(course.getModuleById(moduleId).getTitle(), updatedTitle);
    }

    @Test
    public void shouldUpdateModuleAndDeleteFile() throws Exception {
        String moduleId = "moduleId";
        String courseId = "course-id";
        String url = "test/path/to/file.pdf";
        String newUrl = "test/path/to/file2.pdf";
        Course course = new Course();
        Module module = new FileModule(url, (long) 1024);
        module.setId(moduleId);
        List<Module> modules = new ArrayList<>();
        modules.add(module);
        course.setModules(modules);
        Module newModule = new FileModule(newUrl, (long) 1024);
        newModule.setId(moduleId);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        moduleService.updateModule(courseId, newModule);
        assertEquals(((FileModule) course.getModuleById(moduleId)).getUrl(), newUrl);
        verify(fileUploadService, timeout(2000)).delete(url);
    }

    @Test
    public void shouldDeleteModule() throws Exception {
        String moduleId = "moduleId";
        String courseId = "courseId";
        String url = "https://www.example.org";
        Course course = new Course();
        Module module = new LinkModule(new URL(url));
        module.setId(moduleId);
        List<Module> modules = new ArrayList<>();
        modules.add(module);
        course.setModules(modules);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        moduleService.deleteModule(courseId, moduleId);
        assertTrue(course.getModules().size() == 0);
    }

    @Test
    public void shouldDeleteModuleAndFile() throws Exception {
        String moduleId = "moduleId";
        String courseId = "courseId";
        String url = "test/path/to/file.pdf";
        Course course = new Course();
        Module module = new FileModule(url, (long) 1024);
        module.setId(moduleId);
        List<Module> modules = new ArrayList<>();
        modules.add(module);
        course.setModules(modules);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        moduleService.deleteModule(courseId, moduleId);
        assertTrue(course.getModules().size() == 0);
        verify(fileUploadService, timeout(2000)).delete(url);
    }

    @Test
    public void shouldReturnMapOfIdAndModule() {
        Module module1 = new FaceToFaceModule("a");

        Course course1 = new Course();
        course1.setModules(Collections.singletonList(module1));

        Module module2 = new FaceToFaceModule("b");
        Module module3 = new FaceToFaceModule("c");

        Course course2 = new Course();
        course2.setModules(Arrays.asList(module2, module3));

        when(courseRepository.findAll()).thenReturn(Arrays.asList(course1, course2));

        ModuleDto moduleDto1 = new ModuleDto();
        ModuleDto moduleDto2 = new ModuleDto();
        ModuleDto moduleDto3 = new ModuleDto();

        when(moduleDtoFactory.create(module1, course1)).thenReturn(moduleDto1);
        when(moduleDtoFactory.create(module2, course2)).thenReturn(moduleDto2);
        when(moduleDtoFactory.create(module3, course2)).thenReturn(moduleDto3);

        Map<String, ModuleDto> expected = ImmutableMap.of(
                module1.getId(), moduleDto1,
                module2.getId(), moduleDto2,
                module3.getId(), moduleDto3
        );

        assertEquals(expected, moduleService.getModuleMap());
    }

    @Test
    public void shouldReturnMapOfIdAndModuleByProfession() {
        String profesionId = "profession-id";
        Module module1 = new FaceToFaceModule("a");

        Course course1 = new Course();
        course1.setModules(Collections.singletonList(module1));

        Module module2 = new FaceToFaceModule("b");
        Module module3 = new FaceToFaceModule("c");

        Course course2 = new Course();
        course2.setModules(Arrays.asList(module2, module3));

        when(courseRepository.findAllByProfessionId(profesionId)).thenReturn(Arrays.asList(course1, course2));

        ModuleDto moduleDto1 = new ModuleDto();
        ModuleDto moduleDto2 = new ModuleDto();
        ModuleDto moduleDto3 = new ModuleDto();

        when(moduleDtoFactory.create(module1, course1)).thenReturn(moduleDto1);
        when(moduleDtoFactory.create(module2, course2)).thenReturn(moduleDto2);
        when(moduleDtoFactory.create(module3, course2)).thenReturn(moduleDto3);

        Map<String, ModuleDto> expected = ImmutableMap.of(
                module1.getId(), moduleDto1,
                module2.getId(), moduleDto2,
                module3.getId(), moduleDto3
        );

        assertEquals(expected, moduleService.getModuleMap(profesionId));
    }

}