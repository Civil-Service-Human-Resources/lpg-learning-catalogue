package uk.gov.cslearning.catalogue.dto.factory;

import org.junit.jupiter.api.Test;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.dto.CourseDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CourseDtoFactoryTest {
    private final CourseDtoFactory dtoFactory = new CourseDtoFactory();

    @Test
    public void shouldReturnCourseDto() {
        String id = "course-id";
        String title = "course-title";

        Course course = new Course();
        course.setId(id);
        course.setTitle(title);

        CourseDto dto = dtoFactory.create(course);

        assertEquals(id, dto.getId());
        assertEquals(title, dto.getTitle());
    }
}