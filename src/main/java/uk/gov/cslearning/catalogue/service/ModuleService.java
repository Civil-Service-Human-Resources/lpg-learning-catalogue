package uk.gov.cslearning.catalogue.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.Module;
import uk.gov.cslearning.catalogue.repository.CourseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class ModuleService {
    private final CourseRepository courseRepository;

    public ModuleService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
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
}
