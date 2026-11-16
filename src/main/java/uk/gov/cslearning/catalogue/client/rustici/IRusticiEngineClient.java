package uk.gov.cslearning.catalogue.client.rustici;

import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourse;
import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourseResponse;

public interface IRusticiEngineClient {

    CreateCourseResponse createCourse(CreateCourse requestBody, String rusticiCourseId);

    void deleteCourse(String rusticiCourseId);
}
