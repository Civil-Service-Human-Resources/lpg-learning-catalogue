package uk.gov.cslearning.catalogue.api;

import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry.Option;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.SearchPage;
import uk.gov.cslearning.catalogue.repository.CourseRepository;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(SearchController.class)
@WithMockUser(username = "user")
@EnableSpringDataWebSupport
public class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseRepository courseRepository;

    @Test
    public void shouldReturnSearchPageOfCourses() throws Exception {
        ArrayList<Course> courseArrayList = new ArrayList<>();
        Course course = new Course();
        courseArrayList.add(course);
        Page<Course> coursePage = new PageImpl<>(new ArrayList<>());

        SearchPage searchPage = new SearchPage();
        Option suggestion = new Option(new Text("test-suggestion"), 0.1f);
        searchPage.setTopScoringSuggestion(suggestion);
        searchPage.setCourses(coursePage);

        when(courseRepository.search(any(String.class), any(Pageable.class), any(FilterParameters.class)))
                .thenReturn(searchPage);

        mockMvc.perform(
                get("/search/courses")
                        .param("query", "test")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

}
