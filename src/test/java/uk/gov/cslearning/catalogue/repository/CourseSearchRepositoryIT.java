package uk.gov.cslearning.catalogue.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cslearning.catalogue.domain.Course;

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
    public void tester(){

        List<Course> actualCourseList = repository.suggestions("This text");
        assertEquals(actualCourseList.get(0).getTitle(), "Test");
    }

}
