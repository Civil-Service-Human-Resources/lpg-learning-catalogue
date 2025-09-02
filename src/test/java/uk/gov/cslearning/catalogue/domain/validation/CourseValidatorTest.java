package uk.gov.cslearning.catalogue.domain.validation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.gov.cslearning.catalogue.Utils;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.Status;
import uk.gov.cslearning.catalogue.exception.ForbiddenException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utils.class })
public class CourseValidatorTest {

    CourseValidator validator = new CourseValidator();

    @Test(expected = ForbiddenException.class)
    public void validateInvalidPermission() {
        PowerMockito.mockStatic(Utils.class);
        when(Utils.checkRoles(any())).thenReturn(false);
        Course existingCourse = new Course();
        existingCourse.setStatus(Status.DRAFT);
        Course newCourse = new Course();
        newCourse.setStatus(Status.PUBLISHED);
        validator.validate(existingCourse, newCourse);
    }

    @Test
    public void validateValidPermission() {
        CourseValidator validator = new CourseValidator();
        PowerMockito.mockStatic(Utils.class);
        when(Utils.checkRoles(any())).thenReturn(true);
        Course existingCourse = new Course();
        existingCourse.setStatus(Status.DRAFT);
        Course newCourse = new Course();
        newCourse.setStatus(Status.PUBLISHED);
        validator.validate(existingCourse, newCourse);
        assertDoesNotThrow(() -> validator.validate(existingCourse, newCourse));
    }
}
