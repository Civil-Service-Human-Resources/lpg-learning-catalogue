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
import uk.gov.cslearning.catalogue.service.rustici.RusticiEngineService;
import uk.gov.cslearning.catalogue.service.upload.FileUploadService;
import uk.gov.cslearning.catalogue.service.upload.FileUploadServiceFactory;
import uk.gov.cslearning.catalogue.service.upload.UploadServiceType;

import java.util.*;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

@Service
public class ModuleService {
    private static final int PAGE_SIZE = 10000;

    private final CourseRepository courseRepository;
    private final FileUploadServiceFactory fileUploadServiceFactory;
    private final ModuleDtoFactory moduleDtoFactory;
    private final RusticiEngineService rusticiEngineService;

    public ModuleService(CourseRepository courseRepository,
                         FileUploadServiceFactory fileUploadServiceFactory,
                         ModuleDtoFactory moduleDtoFactory,
                         RusticiEngineService rusticiEngineService) {
        this.courseRepository = courseRepository;
        this.fileUploadServiceFactory = fileUploadServiceFactory;
        this.moduleDtoFactory = moduleDtoFactory;
        this.rusticiEngineService = rusticiEngineService;
    }

    private Course findCourse(String courseId) {
        return courseRepository.findById(courseId).orElseThrow((Supplier<IllegalStateException>) () -> {
            throw new IllegalStateException(
                    String.format("Course does not exist: %s", courseId));
        });
    }

    public Module save(String courseId, Module module) {
        Course course = findCourse(courseId);
        course.addModule(module);
        rusticiEngineService.uploadElearningModule(courseId, module.getId());
        courseRepository.save(course);

        return module;
    }

    public Optional<Module> find(String courseId, String moduleId) {
        return findCourse(courseId).getModules().stream()
                .filter(m -> m.getId().equals(moduleId))
                .findFirst();
    }

    public Course updateModule(String courseId, Module newModule) {
        Course course = findCourse(courseId);

        Module oldModule = course.getModuleById(newModule.getId());
        if (hasFileChanged(newModule, oldModule)) {
            new Thread(() -> deleteFile(courseId, oldModule)).start();
        }

        List<Module> updatedModules = course.getModules().stream()
                .map(m -> m.getId().equals(newModule.getId()) ? newModule : m)
                .collect(toList());

        course.setModules(updatedModules);
        courseRepository.save(course);

        return course;
    }

    public void deleteModule(String courseId, String moduleId) {
        Course course = findCourse(courseId);
        Module module = course.getModuleById(moduleId);

        new Thread(() -> deleteFile(courseId, module)).start();

        course.deleteModule(module);
        courseRepository.save(course);
    }

    private void deleteFile(String courseId, Module module) {
        FileUploadService fileUploadService;
        if (module instanceof FileModule) {
            String filePath = ((FileModule) module).getUrl();
            fileUploadService = fileUploadServiceFactory.getFileUploadService(UploadServiceType.FILE);
            fileUploadService.delete(filePath);
        } else if (module instanceof VideoModule) {
            String filePath = ((VideoModule) module).getUrl().getPath();
            fileUploadService = fileUploadServiceFactory.getFileUploadService(UploadServiceType.MP4);
            fileUploadService.delete(filePath);
        } else if (module instanceof ELearningModule) {
            String filePath = ((ELearningModule) module).getUrl();
            fileUploadService = fileUploadServiceFactory.getFileUploadService(UploadServiceType.SCORM);
            fileUploadService.deleteDirectory(filePath);
            rusticiEngineService.deleteElearningModule(courseId, module.getId());
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
            Page<Course> courses = courseRepository.findAllByIdIn(courseIds, PageRequest.of(page, PAGE_SIZE));
            courses.forEach(c ->
                    c.getModules().forEach(m ->
                            results.put(m.getId(), moduleDtoFactory.create(m, c))));
            page = page + 1;
            numberOfCourses = courses.getNumberOfElements();
        } while(numberOfCourses == PAGE_SIZE);
        return results;
    }

    public Map<String, ModuleDto> getModuleMapForSupplier(String supplier, Pageable pageable) {
        Map<String, ModuleDto> results = new HashMap<>();
        int page = 0;
        int numberOfCourses;
        do {
            Page<Course> courses = courseRepository.findAllBySupplier(supplier, pageable);
            courses.forEach(c ->
                    c.getModules().forEach(m ->
                            results.put(m.getId(), moduleDtoFactory.create(m, c))));
            page = page + 1;
            numberOfCourses = courses.getNumberOfElements();
        } while(numberOfCourses == PAGE_SIZE);

        return results;
    }
}
