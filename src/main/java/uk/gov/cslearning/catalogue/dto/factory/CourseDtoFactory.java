package uk.gov.cslearning.catalogue.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.dto.CourseDto;

@Component
public class CourseDtoFactory {
    private final ModuleDtoFactory moduleDtoFactory;

    public CourseDtoFactory(ModuleDtoFactory moduleDtoFactory) {
        this.moduleDtoFactory = moduleDtoFactory;
    }

    public CourseDto create(Course course) {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(course.getId());
        courseDto.setTitle(course.getTitle());

        return courseDto;
    }
}
