package uk.gov.cslearning.catalogue.service.rustici;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourse;

@Service
public class CSLToRusticiDataService {

    private final String cdnEndpoint;
    private final String RUSTICI_COURSE_ID_FORMAT = "%s.%s";

    public CSLToRusticiDataService(
            @Value("azure.content-cdn") String cdnEndpoint) {
        this.cdnEndpoint = cdnEndpoint;
    }

    public CreateCourse getCreateCourseData(String courseId, String moduleId) {
        String ELearningCdnLocation = getRusticiCourseCdnLocation(courseId, moduleId);
        return CreateCourse.createFromData(
                String.format("%s/imsmanifest.xml", ELearningCdnLocation),
                ELearningCdnLocation);
    }

    public String getRusticiCourseId(String courseId, String moduleId) {
        return String.format(RUSTICI_COURSE_ID_FORMAT, courseId, moduleId);
    }

    public String getRusticiCourseCdnLocation(String courseId, String moduleId) {
        String rusticiCourseId = getRusticiCourseId(courseId, moduleId);
        return String.format("%s/%s", cdnEndpoint, String.format("%s/%s", courseId, moduleId));
    }
}
