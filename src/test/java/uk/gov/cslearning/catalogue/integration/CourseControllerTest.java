package uk.gov.cslearning.catalogue.integration;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cslearning.catalogue.config.DisableBlobStorage;
import uk.gov.cslearning.catalogue.config.IntegrationTestConfig;
import uk.gov.cslearning.catalogue.domain.CivilServant.CivilServant;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.Owner.Owner;
import uk.gov.cslearning.catalogue.domain.Status;
import uk.gov.cslearning.catalogue.domain.Visibility;
import uk.gov.cslearning.catalogue.domain.module.ELearningModule;
import uk.gov.cslearning.catalogue.domain.module.Module;
import uk.gov.cslearning.catalogue.repository.CourseRepository;
import uk.gov.cslearning.catalogue.service.RegistryService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@Import({IntegrationTestConfig.class, DisableBlobStorage.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.main.allow-bean-definition-overriding=true"})
@AutoConfigureMockMvc
public class CourseControllerTest extends ElasticContainerBase {

    @MockBean
    private RegistryService mockRegistryService;

    @Autowired
    private CourseRepository repository;

    private ArrayList<String> courseIds = new ArrayList<>();

    @Autowired
    private MockMvc mvc;

    @Before
    public void before() {
        // Any endpoint annotated with @PreAuthorize will call CsrsService to get the current civil servant.
        // We should mock this call out with wiremock but until then let's just mock the method out
        when(mockRegistryService.getCurrentCivilServant()).thenReturn(new CivilServant());
        loadCourses();
    }

    @After
    public void after() {
        repository.deleteAllById(courseIds);
    }

    private void loadCourses() {
        Course course1 = new Course("Course 1", "Course 1 short description", "Course 1 long description", Visibility.PUBLIC);
        course1.setOwner(new Owner());
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
        course2.setOwner(new Owner());

        List<Course> courses = Arrays.asList(course1, course2);

        repository.saveAll(courses).forEach(c -> courseIds.add(c.getId()));

    }

    @Test
    @Order(1)
    public void testGetCourses() throws Exception {

        mvc.perform(get("/courses")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].title").value("Course 1"))
                .andExpect(jsonPath("$.results[0].shortDescription").value("Course 1 short description"))
                .andExpect(jsonPath("$.results[0].description").value("Course 1 long description"))
                .andExpect(jsonPath("$.results[0].status").value("Published"))
                .andExpect(jsonPath("$.results[1].title").value("Course 2"))
                .andExpect(jsonPath("$.results[1].shortDescription").value("Course 2 short description"))
                .andExpect(jsonPath("$.results[1].description").value("Course 2 long description"))
                .andExpect(jsonPath("$.results[1].status").value("Published"))
                .andExpect(jsonPath("$.results[1].modules[0].title").value("ELearning module"))
                .andExpect(jsonPath("$.results[1].modules[0].description").value("An ELearning module"))
                .andExpect(jsonPath("$.results[1].modules[0].cost").value(100))
                .andExpect(jsonPath("$.results[1].modules[0].type").value("elearning"))
                .andExpect(jsonPath("$.results[1].modules[0].moduleType").value("elearning"))
                .andExpect(jsonPath("$.results[1].modules[0].duration").value(100))
                .andExpect(jsonPath("$.results[1].modules[0].optional").value(false));
    }

    @Test
    @Order(2)
    public void testGetCoursesWithPagination() throws Exception {

        mvc.perform(get("/courses?size=1&page=0")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResults").value(2))
                .andExpect(jsonPath("$.results[0].title").value("Course 1"))
                .andExpect(jsonPath("$.results[0].shortDescription").value("Course 1 short description"))
                .andExpect(jsonPath("$.results[0].description").value("Course 1 long description"))
                .andExpect(jsonPath("$.results[0].status").value("Published"));

        mvc.perform(get("/courses?size=1&page=1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].title").value("Course 2"))
                .andExpect(jsonPath("$.results[0].shortDescription").value("Course 2 short description"))
                .andExpect(jsonPath("$.results[0].description").value("Course 2 long description"))
                .andExpect(jsonPath("$.results[0].status").value("Published"))
                .andExpect(jsonPath("$.results[0].modules[0].title").value("ELearning module"))
                .andExpect(jsonPath("$.results[0].modules[0].description").value("An ELearning module"))
                .andExpect(jsonPath("$.results[0].modules[0].cost").value(100))
                .andExpect(jsonPath("$.results[0].modules[0].duration").value(100))
                .andExpect(jsonPath("$.results[0].modules[0].type").value("elearning"))
                .andExpect(jsonPath("$.results[0].modules[0].moduleType").value("elearning"))
                .andExpect(jsonPath("$.results[0].modules[0].optional").value(false));

    }

    @Test
    @Order(3)
    @WithMockUser(value = "spring", authorities = {"CSL_AUTHOR"})
    public void testCreateModule() throws Exception {

        String json = new JSONObject()
                .put("url", "http://url.com")
                .put("title", "Link module")
                .put("description", "A link module")
                .put("duration",  100)
                .put("cost",  0)
                .put("optional",  false)
                .put("associatedLearning",  false)
                .put("type", "link")
                .toString();

        mvc.perform(post(String.format("/courses/%s/modules", courseIds.get(0)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mvc.perform(get(String.format("/courses/%s", courseIds.get(0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modules[0].title").value("Link module"))
                .andExpect(jsonPath("$.modules[0].description").value("A link module"))
                .andExpect(jsonPath("$.modules[0].cost").value(0))
                .andExpect(jsonPath("$.modules[0].duration").value(100))
                .andExpect(jsonPath("$.modules[0].moduleType").value("link"))
                .andExpect(jsonPath("$.modules[0].type").value("link"))
                .andExpect(jsonPath("$.modules[0].optional").value(false));

    }

}
