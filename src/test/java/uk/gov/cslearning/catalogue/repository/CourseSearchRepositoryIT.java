package uk.gov.cslearning.catalogue.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.SearchPage;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * UserRepository integration test.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class CourseSearchRepositoryIT {

    private PageRequest all = PageRequest.of(0, 1000);

    @Autowired
    private CourseRepository repository;

    @Test
    public void testMisspelledSearchQueryReturnsAccurateSuggestionAndCourse() {
        SearchPage actualSearchPage = repository.search("Wirking with Budgets");

        String actualSuggestionText = actualSearchPage.getTopScoringSuggestion().getText().toString();
        Page<Course> coursePage = actualSearchPage.getCourses();
        List<Course> courseList = coursePage.getContent();
        String actualTitle = courseList.get(0).getTitle();

        assertEquals("working with budgets", actualSuggestionText);
        assertEquals("Working with budgets", actualTitle);
    }

    @Test
    public void testSearchQueryReturnsCorrectCoursePage() {
        SearchPage actualSearchPage = repository.search("Budgets");
        List<Course> actualCourses = actualSearchPage.getCourses().getContent();

        assertEquals(2, actualCourses.size());
        assertEquals("Working with budgets", actualCourses.get(0).getTitle());
        assertEquals("BUfZwRaWQrKAhSSjlJ7lCg", actualCourses.get(0).getId());
        assertEquals("This topic introduces you to the fundamental principles of budget management and governance processes. ", actualCourses.get(0).getShortDescription());
    }
}
