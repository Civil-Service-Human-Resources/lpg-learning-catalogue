package uk.gov.cslearning.catalogue.repository;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cslearning.catalogue.domain.Course;

import java.util.Collection;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
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

    @Test
    public void shouldSaveCourse() {
        Course course = createCourse();
        repository.save(course);
        assertThat(course.getId(), notNullValue());
    }

    @Test
    public void shouldFindCourseById() {

        Course course = createCourse();
        repository.save(course);

        assertThat(course.getId(), notNullValue());

        Course foundCourse = repository.findById(course.getId()).orElse(null);

        assertThat(foundCourse, notNullValue());
        assertThat(foundCourse.getId(), equalTo(course.getId()));
    }

    @Test
    public void shouldFindMandatoryCoursesForDepartment() {

        final int currentCount = repository.findMandatory("co").size();

        Course one = createCourse("one", ImmutableSet.of("mandatory:co"));
        Course two = createCourse("two", ImmutableSet.of("mandatory:co"));

        repository.save(one);
        repository.save(two);

        Collection<Course> mandatoryCourses = repository.findMandatory("co");

        assertThat(mandatoryCourses.size(), is(currentCount + 2));
        assertThat(mandatoryCourses, hasItem(one));
        assertThat(mandatoryCourses, hasItem(two));
    }

    @Test
    public void shouldFindMandatoryCoursesForAll() {

        final int currentCount = repository.findMandatory("co").size();

        Course one = createCourse("one", ImmutableSet.of("mandatory:all"));

        repository.save(one);

        Collection<Course> mandatoryCourses = repository.findMandatory("co");

        assertThat(mandatoryCourses.size(), is(currentCount + 1));
        assertThat(mandatoryCourses, hasItem(one));
    }

    @Test
    public void shouldNotFindMandatoryCoursesForOtherDepartment() {

        final int currentCount = repository.findMandatory("co").size();

        Course one = createCourse("one", ImmutableSet.of("mandatory:hmrc"));

        repository.save(one);

        Collection<Course> mandatoryCourses = repository.findMandatory("co");

        assertThat(mandatoryCourses.size(), is(currentCount));
    }

    private Course createCourse() {
        return createCourse("title", emptySet());
    }

    private Course createCourse(String title, Set<String> tags) {
        return new Course(title, "shortDescription", "description",
                "learningOutcomes", 1000L, tags);
    }
}
