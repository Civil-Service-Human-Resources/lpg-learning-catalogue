package uk.gov.cslearning.catalogue.service;

import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.Module;
import uk.gov.cslearning.catalogue.repository.CourseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ModuleService {
    private final CourseRepository courseRepository;

    public ModuleService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Module save(String courseId, Module module) throws IllegalStateException {
        Optional<Course> courseOptional = courseRepository.findById(courseId);

        courseOptional.orElseThrow((Supplier<IllegalStateException>) () -> {
            throw new IllegalStateException("Unable to add module. Course does not exist: " + courseId);
        });

        Course course = courseOptional.get();
        List<Module> modules = new ArrayList<>(course.getModules());
        modules.add(module);
        course.setModules(modules);
        courseRepository.save(course);

        return module;
    }
}
