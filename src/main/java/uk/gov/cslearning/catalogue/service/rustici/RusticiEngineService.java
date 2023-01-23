package uk.gov.cslearning.catalogue.service.rustici;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.dto.rustici.course.Course;
import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourse;
import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourseResponse;

@Service
@Slf4j
public class RusticiEngineService {
    private final CSLToRusticiDataService rusticiDataService;
    private final RusticiEngineClient rusticiEngineClient;

    public RusticiEngineService(
            CSLToRusticiDataService rusticiDataService,
            RusticiEngineClient rusticiEngineClient) {
        this.rusticiDataService = rusticiDataService;
        this.rusticiEngineClient = rusticiEngineClient;
    }

    public void uploadElearningModule(String courseId, String moduleId) {
        CreateCourse data = rusticiDataService.getCreateCourseData(courseId, moduleId);
        String rusticiCourseId = rusticiDataService.getRusticiCourseId(courseId, moduleId);
        CreateCourseResponse createCourseResponse = rusticiEngineClient.createCourse(data, rusticiCourseId);
        if (!createCourseResponse.getParserWarnings().isEmpty()) {
            log.warn("Parser warnings reported when uploading course");
            log.warn(String.join(",\n", createCourseResponse.getParserWarnings()));
        }
        Course courseObj = createCourseResponse.getCourse();
        log.info(String.format("Successfully uploaded %s course \"%s\" to Rustici", courseObj.getCourseLearningStandard(), courseObj.getTitle()));
    }

    public void deleteElearningModule(String courseId, String moduleId) {
        String rusticiCourseId = rusticiDataService.getRusticiCourseId(courseId, moduleId);
        rusticiEngineClient.deleteCourse(rusticiCourseId);
        log.info(String.format("Successfully deleted course from Rustici with ID %s", rusticiCourseId));
    }

}
