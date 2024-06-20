package uk.gov.cslearning.catalogue.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import uk.gov.cslearning.catalogue.config.DisableBlobStorage;
import uk.gov.cslearning.catalogue.config.IntegrationTestConfig;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.Status;
import uk.gov.cslearning.catalogue.domain.Visibility;
import uk.gov.cslearning.catalogue.domain.module.ELearningModule;
import uk.gov.cslearning.catalogue.domain.module.Module;
import uk.gov.cslearning.catalogue.repository.CourseRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@Import({IntegrationTestConfig.class, DisableBlobStorage.class})
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.main.allow-bean-definition-overriding=true"})
public class CourseControllerTest extends ElasticContainerBase {

    @Autowired
    private CourseRepository repository;

    private ArrayList<String> courseIds = new ArrayList<>();

    @Autowired
    private WebTestClient webTestClient;

    @Before
    public void before() {
        loadCourses();
    }

    @After
    public void after() {
        repository.deleteAllById(courseIds);
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

        repository.saveAll(courses).forEach(c -> courseIds.add(c.getId()));
    }

    @Test
    public void testGetCourses() {
        webTestClient.get()
                .uri("/courses")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.totalResults").isEqualTo(2)
                .jsonPath("$.results[0].title").isEqualTo("Course 1")
                .jsonPath("$.results[0].shortDescription").isEqualTo("Course 1 short description")
                .jsonPath("$.results[0].description").isEqualTo("Course 1 long description")
                .jsonPath("$.results[0].status").isEqualTo("Published")
                .jsonPath("$.results[1].title").isEqualTo("Course 2")
                .jsonPath("$.results[1].shortDescription").isEqualTo("Course 2 short description")
                .jsonPath("$.results[1].description").isEqualTo("Course 2 long description")
                .jsonPath("$.results[1].status").isEqualTo("Published")
                .jsonPath("$.results[1].modules[0].title").isEqualTo("ELearning module")
                .jsonPath("$.results[1].modules[0].description").isEqualTo("An ELearning module")
                .jsonPath("$.results[1].modules[0].cost").isEqualTo(100)
                .jsonPath("$.results[1].modules[0].duration").isEqualTo(100)
                .jsonPath("$.results[1].modules[0].optional").isEqualTo(false);
    }

    @Test
    public void testGetCoursesWithPagination() {
        webTestClient.get()
                .uri("/courses?size=1&page=0")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.totalResults").isEqualTo(2)
                .jsonPath("$.results[0].title").isEqualTo("Course 1")
                .jsonPath("$.results[0].shortDescription").isEqualTo("Course 1 short description")
                .jsonPath("$.results[0].description").isEqualTo("Course 1 long description")
                .jsonPath("$.results[0].status").isEqualTo("Published");

        webTestClient.get()
                .uri("/courses?size=1&page=1")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.results[0].title").isEqualTo("Course 2")
                .jsonPath("$.results[0].shortDescription").isEqualTo("Course 2 short description")
                .jsonPath("$.results[0].description").isEqualTo("Course 2 long description")
                .jsonPath("$.results[0].status").isEqualTo("Published")
                .jsonPath("$.results[0].modules[0].title").isEqualTo("ELearning module")
                .jsonPath("$.results[0].modules[0].description").isEqualTo("An ELearning module")
                .jsonPath("$.results[0].modules[0].cost").isEqualTo(100)
                .jsonPath("$.results[0].modules[0].duration").isEqualTo(100)
                .jsonPath("$.results[0].modules[0].optional").isEqualTo(false);
    }

}
