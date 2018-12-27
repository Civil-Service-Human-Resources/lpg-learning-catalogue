package uk.gov.cslearning.catalogue.dto.factory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.FaceToFaceModule;
import uk.gov.cslearning.catalogue.dto.CourseDto;
import uk.gov.cslearning.catalogue.dto.ModuleDto;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ModuleDtoFactoryTest {
    @Mock
    private CourseDtoFactory courseDtoFactory;

    @InjectMocks
    private ModuleDtoFactory dtoFactory;

    @Test
    public void shouldReturnRequiredModuleDto() {
        String id = "module-id";
        String title = "module-title";

        FaceToFaceModule module = new FaceToFaceModule("abc");
        module.setOptional(false);
        module.setId(id);
        module.setTitle(title);

        Course course = new Course();
        CourseDto courseDto = new CourseDto();

        when(courseDtoFactory.create(course)).thenReturn(courseDto);

        ModuleDto dto = dtoFactory.create(module, course);
        assertEquals(id, dto.getId());
        assertEquals(title, dto.getTitle());
        assertTrue(dto.isRequired());
        assertEquals(courseDto, dto.getCourse());

        verify(courseDtoFactory).create(course);
    }

    @Test
    public void shouldReturnModuleDto() {
        String id = "module-id";
        String title = "module-title";

        FaceToFaceModule module = new FaceToFaceModule("abc");
        module.setOptional(true);
        module.setId(id);
        module.setTitle(title);

        Course course = new Course();
        CourseDto courseDto = new CourseDto();

        when(courseDtoFactory.create(course)).thenReturn(courseDto);

        ModuleDto dto = dtoFactory.create(module, course);
        assertEquals(id, dto.getId());
        assertEquals(title, dto.getTitle());
        assertFalse(dto.isRequired());
        assertEquals(courseDto, dto.getCourse());
        assertEquals("face-to-face", dto.getType());

        verify(courseDtoFactory).create(course);
    }
}