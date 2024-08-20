package uk.gov.cslearning.catalogue.integration;

import org.elasticsearch.common.collect.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cslearning.catalogue.config.DisableBlobStorage;
import uk.gov.cslearning.catalogue.config.IntegrationTestConfig;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.Owner.Owner;
import uk.gov.cslearning.catalogue.domain.Status;
import uk.gov.cslearning.catalogue.domain.Visibility;
import uk.gov.cslearning.catalogue.domain.module.Audience;
import uk.gov.cslearning.catalogue.repository.CourseRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@Import({IntegrationTestConfig.class, DisableBlobStorage.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.main.allow-bean-definition-overriding=true"})
@AutoConfigureMockMvc
public class CourseControllerV2Test extends ElasticContainerBase {

    @Autowired
    private CourseRepository repository;

    private ArrayList<String> courseIds = new ArrayList<>();

    @Autowired
    private MockMvc mvc;

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
        course1.setId("course1");
        course1.setStatus(Status.PUBLISHED);
        course1.setOwner(new Owner());
        Audience audience = new Audience();
        audience.setDepartments(Set.of("HMRC", "CO"));
        audience.setType(Audience.Type.REQUIRED_LEARNING);
        audience.setRequiredBy(Instant.now());
        Audience audience2 = new Audience();
        audience2.setDepartments(Set.of("DWP"));
        audience2.setType(Audience.Type.OPEN);
        course1.setAudiences(Set.of(audience, audience2));

        Course course2 = new Course("Course 2", "Course 2 short description", "Course 2 long description", Visibility.PUBLIC);
        course2.setId("course2");
        course2.setStatus(Status.PUBLISHED);
        course2.setOwner(new Owner());
        Audience audience3 = new Audience();
        audience3.setDepartments(Set.of("HMRC"));
        audience3.setType(Audience.Type.REQUIRED_LEARNING);
        audience3.setRequiredBy(Instant.now());
        Audience audience4 = new Audience();
        audience4.setDepartments(Set.of("CO"));
        audience4.setType(Audience.Type.OPEN);
        course2.setAudiences(Set.of(audience3, audience4));

        List<Course> courses = Arrays.asList(course1, course2);

        repository.saveAll(courses).forEach(c -> courseIds.add(c.getId()));

    }

    @Test
    public void testGetRequiredLearningDepartmentMap() throws Exception {
        mvc.perform(get("/v2/courses/required-learning-map")
                .with(csrf()))
                .andExpect(jsonPath("$.departmentCodeMap.HMRC[0]").value("course1"))
                .andExpect(jsonPath("$.departmentCodeMap.HMRC[1]").value("course2"))
                .andExpect(jsonPath("$.departmentCodeMap.CO[0]").value("course1"));
    }

}
