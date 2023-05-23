package uk.gov.cslearning.catalogue.api.v2;

import lombok.SneakyThrows;
import org.glassfish.jersey.servlet.WebConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cslearning.catalogue.api.v2.model.GetCoursesParameters;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.repository.CourseRepository;

import java.util.Arrays;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(CourseControllerV2.class)
@WithMockUser(username = "user")
@ContextConfiguration(classes = {WebConfig.class, CourseControllerV2.class})
@EnableSpringDataWebSupport
public class CourseControllerV2Test {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseRepository courseRepository;

    @Test
    public void testGetCoursesDefaultParameters() throws Exception {
        GetCoursesParameters params = new GetCoursesParameters();
        Course course = new Course();
        course.setId("TestID");
        when(courseRepository.findSuggested(eq(params), any(Pageable.class)))
                .thenReturn(new PageImpl<>(singletonList(course)));

        mockMvc.perform(get("/v2/courses/")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id", equalTo(course.getId())));
    }

    @Test
    public void testGetCoursesParameters() throws Exception {
        GetCoursesParameters params = new GetCoursesParameters();
        params.setDepartments(Arrays.asList("dep1", "dep2"));
        params.setGrade("G7");
        params.setInterest("Interest");
        params.setAreaOfWork("AOW");
        params.setExcludeAreasOfWork(Arrays.asList("AOW2", "AOW3"));
        params.setExcludeDepartments(Arrays.asList("dep3", "dep4"));
        params.setExcludeInterests(Arrays.asList("interest2", "interest3"));
        params.setStatus("ARCHIVED");
        Course course = new Course();
        course.setId("TestID");
        when(courseRepository.findSuggested(eq(params), any(Pageable.class)))
                .thenReturn(new PageImpl<>(singletonList(course)));

        mockMvc.perform(get("/v2/courses")
                        .param("departments", "dep1,dep2")
                        .param("grade", "G7")
                        .param("interest", "Interest")
                        .param("areaOfWork", "AOW")
                        .param("excludeAreasOfWork", "AOW2,AOW3")
                        .param("excludeDepartments", "dep3,dep4")
                        .param("excludeInterests", "interest2,interest3")
                        .param("status", "ARCHIVED")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[0].id", equalTo(course.getId())));
    }
}
