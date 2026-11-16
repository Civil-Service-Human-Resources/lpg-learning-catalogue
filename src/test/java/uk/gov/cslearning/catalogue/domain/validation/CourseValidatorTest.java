package uk.gov.cslearning.catalogue.domain.validation;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import uk.gov.cslearning.catalogue.Utils;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.Status;
import uk.gov.cslearning.catalogue.exception.ForbiddenException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

public class CourseValidatorTest {

    CourseValidator validator = new CourseValidator();

    @Test
    public void validateInvalidPermission() {
        // Use Mockito's MockedStatic in a try-with-resources block to automatically deregister it
        try (MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.checkRoles(any())).thenReturn(false);

            Course existingCourse = new Course();
            existingCourse.setStatus(Status.DRAFT);
            Course newCourse = new Course();
            newCourse.setStatus(Status.PUBLISHED);

            assertThrows(ForbiddenException.class, () -> validator.validate(existingCourse, newCourse));
        }
    }

    @Test
    public void validateValidPermission() {
        try (MockedStatic<Utils> mockedUtils = Mockito.mockStatic(Utils.class)) {
            mockedUtils.when(() -> Utils.checkRoles(any())).thenReturn(true);

            Course existingCourse = new Course();
            existingCourse.setStatus(Status.DRAFT);
            Course newCourse = new Course();
            newCourse.setStatus(Status.PUBLISHED);

            assertDoesNotThrow(() -> validator.validate(existingCourse, newCourse));
        }
    }
}
