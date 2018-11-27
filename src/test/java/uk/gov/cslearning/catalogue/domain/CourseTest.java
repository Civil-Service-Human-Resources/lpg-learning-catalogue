package uk.gov.cslearning.catalogue.domain;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.catalogue.domain.module.LinkModule;
import uk.gov.cslearning.catalogue.domain.module.Module;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@Transactional
public class CourseTest {

    public static final String URL = "https://www.example.com";
    public static final String MODULE_ID = "module-id";

    @Test
    public void shouldRemoveModuleFromCourse() throws Exception {
        Course course = createCourse();
        Module module1 = new LinkModule(new URL(URL));
        module1.setId(MODULE_ID);

        List<Module> moduleList = new ArrayList<>();
        moduleList.add(module1);
        course.setModules(moduleList);

        assertThat(course.getModules().size(), equalTo(1));

        course.deleteModule(module1);

        assertThat(course.getModules().size(), equalTo(0));
    }

    @Test
    public void shouldNotRemoveModuleFromCourseIfDoesntExist() throws Exception {
        Course course = createCourse();
        Module module1 = new LinkModule(new URL(URL));
        module1.setId(MODULE_ID);

        Module module2 = new LinkModule(new URL(URL));
        module1.setId("module-id-2");

        List<Module> moduleList = new ArrayList<>();
        moduleList.add(module1);
        course.setModules(moduleList);

        assertThat(course.getModules().size(), equalTo(1));

        course.deleteModule(module2);

        assertThat(course.getModules().size(), equalTo(1));
    }

    private Course createCourse() {
        return new Course("title", "shortDescription", "description",
                Visibility.PUBLIC);
    }
}
