package uk.gov.cslearning.catalogue.integration;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.Status;
import uk.gov.cslearning.catalogue.domain.Visibility;
import uk.gov.cslearning.catalogue.domain.module.ELearningModule;
import uk.gov.cslearning.catalogue.domain.module.Module;
import uk.gov.cslearning.catalogue.repository.CourseRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class BaseIntegrationTest extends ElasticContainerBase {

    @Autowired
    protected CourseRepository repository;

    @Autowired
    protected WebTestClient webTestClient;

    @Before
    public void before() {
        loadCourses();
    }

    private void loadCourses() {
        Course course1 = new Course("Course 1", "Course 1 short description", "Course 1 long description", Visibility.PUBLIC);
        course1.setStatus(Status.PUBLISHED);

        Module elearningModule = new ELearningModule("http://startPage", "http://url.com");
        elearningModule.setTitle("ELearning module");
        elearningModule.setDescription("An ELearning module");
        elearningModule.setCost(BigDecimal.valueOf(100L));
        elearningModule.setDuration(100L);
        elearningModule.setOptional(false);

        Course course2 = new Course("Course 2", "Course 2 short description", "Course 2 long description", Visibility.PUBLIC);
        course2.setStatus(Status.PUBLISHED);

        course2.setModules(Arrays.asList(elearningModule));

        List<Course> courses = Arrays.asList(course1, course2);

        repository.saveAll(courses);
    }
}
