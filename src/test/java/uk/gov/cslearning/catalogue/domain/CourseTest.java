package uk.gov.cslearning.catalogue.domain;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.catalogue.domain.module.FaceToFaceModule;
import uk.gov.cslearning.catalogue.domain.module.LinkModule;
import uk.gov.cslearning.catalogue.domain.module.Module;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Transactional
public class CourseTest {

    public static final String URL = "https://www.example.com";
    public static final String MODULE_ID = "module-id";

    @Test
    public void shouldRemoveModuleFromCourse() throws Exception {
        Course course = createCourse();
        Module module1 = new LinkModule(URI.create(URL).toURL());
        module1.setId(MODULE_ID);

        List<Module> moduleList = new ArrayList<>();
        moduleList.add(module1);
        course.setModules(moduleList);

        assertEquals(1, course.getModules().size());

        course.deleteModule(module1);

        assertEquals(0, course.getModules().size());
    }

    @Test
    public void shouldNotRemoveModuleFromCourseIfDoesntExist() throws Exception {
        Course course = createCourse();
        Module module1 = new LinkModule(URI.create(URL).toURL());
        module1.setId(MODULE_ID);

        Module module2 = new LinkModule(URI.create(URL).toURL());
        module1.setId("module-id-2");

        List<Module> moduleList = new ArrayList<>();
        moduleList.add(module1);
        course.setModules(moduleList);

        assertEquals(1, course.getModules().size());

        course.deleteModule(module2);

        assertEquals(1, course.getModules().size());
    }

    @Test
    public void shouldUpdateCourseCostCorrectly() {
        Course course = createCourse();
        Module module1 = new FaceToFaceModule("");
        module1.setCost(BigDecimal.valueOf(100.00));
        Module module2 = new FaceToFaceModule("");
        module2.setCost(BigDecimal.valueOf(50.24));
        Module module3 = new FaceToFaceModule("");
        module3.setCost(BigDecimal.valueOf(01.00));

        course.setModules(Arrays.asList(module1, module2, module3));
        course.setCostFromModules();
        assertEquals(BigDecimal.valueOf(151.24), course.getCost());

        course.deleteModule(module1);
        course.setCostFromModules();
        assertEquals(BigDecimal.valueOf(51.24), course.getCost());
    }

    private Course createCourse() {
        return new Course("title", "shortDescription", "description",
                Visibility.PUBLIC);
    }
}
