package uk.gov.cslearning.catalogue.integration;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import uk.gov.cslearning.catalogue.domain.CivilServant.CivilServant;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.Status;
import uk.gov.cslearning.catalogue.service.RegistryService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CourseControllerTest extends IntegrationTestBase {

    @MockBean
    private RegistryService mockRegistryService;

    @Before
    public void before() {
        // Any endpoint annotated with @PreAuthorize will call CsrsService to get the current civil servant.
        // We should mock this call out with wiremock but until then let's just mock the method out
        when(mockRegistryService.getCurrentCivilServant()).thenReturn(new CivilServant());
    }


    @Test
    @Order(1)
    public void testGetCourses() throws Exception {

        mvc.perform(get("/courses")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].title").value("Required course 1"))
                .andExpect(jsonPath("$.results[0].shortDescription").value("Required course 1 short description"))
                .andExpect(jsonPath("$.results[0].description").value("Required course 1 long description"))
                .andExpect(jsonPath("$.results[0].status").value("Published"))
                .andExpect(jsonPath("$.results[1].title").value("Required course 2"))
                .andExpect(jsonPath("$.results[1].shortDescription").value("Required course 2 short description"))
                .andExpect(jsonPath("$.results[1].description").value("Required course 2 long description"))
                .andExpect(jsonPath("$.results[1].status").value("Published"));
    }

    @Test
    @Order(2)
    public void testGetCoursesWithPagination() throws Exception {

        mvc.perform(get("/courses?size=1&page=0")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResults").value(13))
                .andExpect(jsonPath("$.results[0].title").value("Required course 1"))
                .andExpect(jsonPath("$.results[0].shortDescription").value("Required course 1 short description"))
                .andExpect(jsonPath("$.results[0].description").value("Required course 1 long description"))
                .andExpect(jsonPath("$.results[0].status").value("Published"));

        mvc.perform(get("/courses?size=1&page=1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].title").value("Required course 2"))
                .andExpect(jsonPath("$.results[0].shortDescription").value("Required course 2 short description"))
                .andExpect(jsonPath("$.results[0].description").value("Required course 2 long description"))
                .andExpect(jsonPath("$.results[0].status").value("Published"));

    }

    @Test
    @Order(2)
    public void testGetMandatoryLearning() throws Exception {

        mvc.perform(get("/courses?mandatory=true&days=7&size=1000000000")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.HMRC[0].title").value("Required course 2"))
                .andExpect(jsonPath("$.HMRC[0].shortDescription").value("Required course 2 short description"))
                .andExpect(jsonPath("$.HMRC[0].description").value("Required course 2 long description"))
                .andExpect(jsonPath("$.HMRC[0].status").value("Published"));

    }

    @Test
    @Order(3)
    @WithMockUser(value = "spring", authorities = {"CSL_AUTHOR"})
    public void testCreateModule() throws Exception {

        Course tempCourse = dataService.createCourse("CreateModule");
        dataService.getRepository().save(tempCourse);

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

        mvc.perform(post(String.format("/courses/%s/modules", tempCourse.getId()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mvc.perform(get(String.format("/courses/%s", tempCourse.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modules[0].title").value("Link module"))
                .andExpect(jsonPath("$.modules[0].description").value("A link module"))
                .andExpect(jsonPath("$.modules[0].cost").value(0))
                .andExpect(jsonPath("$.modules[0].duration").value(100))
                .andExpect(jsonPath("$.modules[0].moduleType").value("link"))
                .andExpect(jsonPath("$.modules[0].type").value("link"))
                .andExpect(jsonPath("$.modules[0].optional").value(false));

        dataService.getRepository().delete(tempCourse);

    }

    @Test
    @Order(4)
    @WithMockUser(value = "spring", authorities = {"LEARNING_UNARCHIVE"})
    public void testUnarchiveCourse() throws Exception {
        Course tempCourse = dataService.createCourse("archiveCourse");
        tempCourse.setStatus(Status.ARCHIVED);
        dataService.getRepository().save(tempCourse);

        Course update = dataService.getRepository().findById(tempCourse.getId()).get();
        update.setStatus(Status.DRAFT);
        mvc.perform(put(String.format("/courses/%s", tempCourse.getId()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isOk());
        assertEquals(Status.DRAFT, dataService.getRepository().findById(tempCourse.getId()).get().getStatus());
    }

    @Test
    @Order(5)
    @WithMockUser(value = "spring", authorities = {"LEARNING_PUBLISH"})
    public void testUnarchiveCourseIncorrectPermission() throws Exception {
        Course tempCourse = dataService.createCourse("archiveCourse");
        tempCourse.setStatus(Status.ARCHIVED);
        dataService.getRepository().save(tempCourse);

        Course update = dataService.getRepository().findById(tempCourse.getId()).get();
        update.setStatus(Status.DRAFT);
        mvc.perform(put(String.format("/courses/%s", tempCourse.getId()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isForbidden());
    }

}
