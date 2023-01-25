package uk.gov.cslearning.catalogue.service.rustici;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.dto.rustici.course.Course;
import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourse;
import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourseResponse;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RusticiEngineServiceTest {

    @Mock
    private CSLToRusticiDataService rusticiDataService;
    @Mock
    private RusticiEngineClient rusticiEngineClient;
    @InjectMocks
    private RusticiEngineService rusticiEngineService;

    private final String courseId = "courseId";
    private final String moduleId = "moduleId";

    @Test
    public void testSuccessfullyUploadElearningModule() {
        String rusticiCourseId = String.format("%s.%s", courseId, moduleId);
        Course course = mock(Course.class);
        when(course.getTitle()).thenReturn("Test title");
        when(course.getCourseLearningStandard()).thenReturn("SCORM2");
        CreateCourse createCourse = mock(CreateCourse.class);
        CreateCourseResponse createCourseResponse = mock(CreateCourseResponse.class);
        when(createCourseResponse.getCourse()).thenReturn(course);
        when(rusticiDataService.getCreateCourseData(courseId, moduleId)).thenReturn(createCourse);
        when(rusticiDataService.getRusticiCourseId(courseId, moduleId)).thenReturn(rusticiCourseId);
        when(rusticiEngineClient.createCourse(createCourse, rusticiCourseId)).thenReturn(createCourseResponse);

        rusticiEngineService.uploadElearningModule(courseId, moduleId);
        verify(rusticiEngineClient).createCourse(createCourse, rusticiCourseId);
    }

    @Test
    public void testSuccessfullyDeleteElearningModule() {
        String rusticiCourseId = String.format("%s.%s", courseId, moduleId);
        when(rusticiDataService.getRusticiCourseId(courseId, moduleId)).thenReturn(rusticiCourseId);
        rusticiEngineService.deleteElearningModule(courseId, moduleId);
        verify(rusticiEngineClient).deleteCourse(rusticiCourseId);
    }
}
