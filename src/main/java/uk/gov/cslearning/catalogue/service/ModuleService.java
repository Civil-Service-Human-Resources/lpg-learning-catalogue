package uk.gov.cslearning.catalogue.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.ELearningModule;
import uk.gov.cslearning.catalogue.domain.module.FileModule;
import uk.gov.cslearning.catalogue.domain.module.Module;
import uk.gov.cslearning.catalogue.domain.module.VideoModule;
import uk.gov.cslearning.catalogue.dto.ModuleDto;
import uk.gov.cslearning.catalogue.dto.factory.ModuleDtoFactory;
import uk.gov.cslearning.catalogue.repository.CourseRepository;
import uk.gov.cslearning.catalogue.service.upload.FileUploadService;

import java.util.*;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

@Service
public class ModuleService {
    private static final int PAGE_SIZE = 10000;

    private final CourseRepository courseRepository;
    private final FileUploadService fileUploadService;
    private final ModuleDtoFactory moduleDtoFactory;

    public ModuleService(CourseRepository courseRepository, FileUploadService fileUploadService, ModuleDtoFactory moduleDtoFactory) {
        this.courseRepository = courseRepository;
        this.fileUploadService = fileUploadService;
        this.moduleDtoFactory = moduleDtoFactory;
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

        Module oldModule = course.getModuleById(newModule.getId());
        if (hasFileChanged(newModule, oldModule)) {
            new Thread(() -> deleteFile(oldModule)).start();
        }

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

        new Thread(() -> deleteFile(module)).start();

        course.deleteModule(module);
        courseRepository.save(course);
    }

    private void deleteFile(Module module) {
        if (module instanceof FileModule) {
            String filePath = ((FileModule) module).getUrl();
            fileUploadService.delete(filePath);
        } else if (module instanceof VideoModule) {
            String filePath = ((VideoModule) module).getUrl().getPath();
            fileUploadService.delete(filePath);
        } else if (module instanceof ELearningModule) {
            String filePath = ((ELearningModule) module).getUrl();
            fileUploadService.deleteDirectory(filePath);
        }
    }

    private boolean hasFileChanged(Module newModule, Module oldModule) {
        return newModule.getClass() != oldModule.getClass()
                || (newModule instanceof FileModule && ((FileModule) newModule).getMediaId() != null && !((FileModule) newModule).getUrl().equals(((FileModule) oldModule).getUrl()))
                || (newModule instanceof VideoModule && ((VideoModule) newModule).getUrl() != null && !((VideoModule) newModule).getUrl().equals(((VideoModule) oldModule).getUrl()))
                || (newModule instanceof ELearningModule && ((ELearningModule) newModule).getUrl() != null && !((ELearningModule) newModule).getUrl().equals(((ELearningModule) oldModule).getUrl()));
    }

    public Map<String, ModuleDto> getModuleMap() {
        Map<String, ModuleDto> results = new HashMap<>();

        for (Course course : courseRepository.findAll()) {
            for (Module module : course.getModules()) {
                results.put(module.getId(), moduleDtoFactory.create(module, course));
            }
        }

        return results;
    }

    public Map<String, ModuleDto> getModuleMapForCourseIds(List<String> courseIds) {

        Map<String, ModuleDto> results = new HashMap<>();
        int page = 0;
        int numberOfCourses;
        do {
            PageRequest pageRequest = PageRequest.of(page, PAGE_SIZE);
            Page<Course> courses = courseRepository.findAllByIdIn(courseIds, pageRequest);
            //Below two for loops need to be changed to the stream
            for (Course course : courses) {
                for (Module module : course.getModules()) {
                    results.put(module.getId(), moduleDtoFactory.create(module, course));
                }
            }
            page = page + 1;
            numberOfCourses = courses.getSize();
        } while(numberOfCourses == PAGE_SIZE);
        return results;
    }

    public Map<String, ModuleDto> getModuleMapForSupplier(String supplier, Pageable pageable) {
        Map<String, ModuleDto> results = new HashMap<>();

        for (Course course : courseRepository.findAllBySupplier(supplier, pageable)) {
            for (Module module : course.getModules()) {
                results.put(module.getId(), moduleDtoFactory.create(module, course));
            }
        }

        return results;
    }
}
