package uk.gov.cslearning.catalogue.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.OnlineModule;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * UserRepository integration test.
 */
@ActiveProfiles({"default", "test"})
@RunWith(SpringRunner.class)
@SpringBootTest
public class CourseRepositoryIT {

    @Autowired
    private CourseRepository repository;

    @Before
    public void setup() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void shouldSaveCourse() {
        Course course = new Course("title", "shortDescription", "description", "learningOutcomes", 1000, Collections.emptySet());
        repository.save(course);
        assertThat(course.getId(), notNullValue());
    }

    @Test
    public void shouldFindCourseById() {
        Course course = new Course("title", "shortDescription", "description", "learningOutcomes", 1000, Collections.emptySet());
        repository.save(course);

        assertThat(course.getId(), notNullValue());

        Course foundCourse = repository.findById(course.getId()).orElse(null);

        assertThat(foundCourse, notNullValue());
        assertThat(foundCourse.getId(), equalTo(course.getId()));
    }
}
