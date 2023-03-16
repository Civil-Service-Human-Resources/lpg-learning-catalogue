package uk.gov.cslearning.catalogue.service.rustici;

import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourse;

public class CSLToRusticiDataService {

    private final String cdnEndpoint;
    private final String RUSTICI_COURSE_ID_FORMAT = "%s.%s";

    public CSLToRusticiDataService(String cdnEndpoint) {
        this.cdnEndpoint = cdnEndpoint;
    }

    public CreateCourse getCreateCourseData(String courseId, String mediaId, String manifestFile) {
        String ELearningCdnLocation = getRusticiCourseCdnLocation(courseId, mediaId);
        String imsManifestUrl = String.format("%s/%s", ELearningCdnLocation, manifestFile);
        return CreateCourse.createFromData(
                ELearningCdnLocation,
                imsManifestUrl);
    }

    public String getRusticiCourseId(String courseId, String moduleId) {
        return String.format(RUSTICI_COURSE_ID_FORMAT, courseId, moduleId);
    }

    public String getRusticiCourseCdnLocation(String courseId, String mediaId) {
        return String.format("%s/%s/%s", cdnEndpoint, courseId, mediaId);
    }
}
