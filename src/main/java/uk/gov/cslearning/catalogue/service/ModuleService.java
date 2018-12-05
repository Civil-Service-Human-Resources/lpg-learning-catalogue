package uk.gov.cslearning.catalogue.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.FileModule;
import uk.gov.cslearning.catalogue.domain.module.Module;
import uk.gov.cslearning.catalogue.domain.module.VideoModule;
import uk.gov.cslearning.catalogue.repository.CourseRepository;
import uk.gov.cslearning.catalogue.service.upload.FileUploadService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

@Service
public class ModuleService {
    private final CourseRepository courseRepository;
    private final FileUploadService fileUploadService;

    public ModuleService(CourseRepository courseRepository, FileUploadService fileUploadService) {
        this.courseRepository = courseRepository;
        this.fileUploadService = fileUploadService;
    }

    public Module save(String courseId, Module module) {
        Course course = courseRepository.findById(courseId).orElseThrow((Supplier<IllegalStateException>) () -> {
            throw new IllegalStateException(
                    String.format("Unable to add module. Course does not exist: %s", courseId));
        });

        List<Module> modules = new ArrayList<>(course.getModules());
        modules.add(module);
        course.setModules(modules);
        courseRepository.save(course);

        return module;
    }

    public Optional<Module> find(String courseId, String moduleId) {
        Course course = courseRepository.findById(courseId).orElseThrow((Supplier<IllegalStateException>) () -> {
            throw new IllegalStateException(
                    String.format("Unable to find module: %s. Course does not exist: %s", moduleId, courseId));
        });

        return course.getModules().stream()
                .filter(m -> m.getId().equals(moduleId))
                .findFirst();
    }

    public Course updateModule(String courseId, Module newModule) {
        Course course = courseRepository.findById(courseId).orElseThrow((Supplier<IllegalStateException>) () -> {
            throw new IllegalStateException(
                    String.format("Unable to add module. Course does not exist: %s", courseId));
        });

        List<Module> updatedModules = course.getModules().stream()
                .map(m -> m.getId().equals(newModule.getId()) ? newModule : m)
                .collect(toList());

        course.setModules(updatedModules);

        courseRepository.save(course);

        return course;
    }

    public void deleteModule(String courseId, String moduleId) {
        Course course = courseRepository.findById(courseId).orElseThrow((Supplier<IllegalStateException>) () -> {
            throw new IllegalStateException(
                    String.format("Unable to add module. Course does not exist: %s", courseId));
        });

        Module module = course.getModuleById(moduleId);

        if(module instanceof FileModule) {
            String filePath = ((FileModule) module).getUrl();
            fileUploadService.delete(filePath);
        } else if (module instanceof VideoModule) {
            String filePath = ((VideoModule) module).getUrl().getPath();
            fileUploadService.delete(filePath);
        }

        course.deleteModule(module);
        courseRepository.save(course);
    }
}
