package uk.gov.cslearning.catalogue.repository;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cslearning.catalogue.domain.Course;

import java.util.Collection;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * UserRepository integration test.
 */
@ActiveProfiles({"default", "test"})
@RunWith(SpringRunner.class)
@SpringBootTest
public class CourseRepositoryIT {

    private PageRequest all = PageRequest.of(0, 1000);

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

    @Test
    public void shouldFindSuggestedLearningByAreaOfWork() {

        final String department = "co";
        final String areaOfWork = "commercial";

        final int currentCount = repository.findSuggested(department, areaOfWork, all).size();

        repository.save(createCourse("one", ImmutableSet.of("area-of-work:" + areaOfWork)));
        repository.save(createCourse("two", ImmutableSet.of("area-of-work:" + areaOfWork)));
        repository.save(createCourse("three", ImmutableSet.of("area-of-work:" + areaOfWork)));

        Collection<Course> courses = repository.findSuggested(department, areaOfWork, all);

        assertThat(courses.size(), is(currentCount + 3));
    }

    @Test
    public void shouldFindSuggestedLearningByDepartment() {

        final String department = "co";
        final String areaOfWork = "commercial";

        final int currentCount = repository.findSuggested(department, areaOfWork, all).size();

        repository.save(createCourse("one", ImmutableSet.of("department:" + department)));
        repository.save(createCourse("two", ImmutableSet.of("department:" + department)));
        repository.save(createCourse("three", ImmutableSet.of("department:" + department)));

        Collection<Course> courses = repository.findSuggested(department, areaOfWork, all);

        assertThat(courses.size(), is(currentCount + 3));
    }

    @Test
    public void shouldFindSuggestedLearningByAreaOfWorkOrDepartment() {

        final String department = "co";
        final String areaOfWork = "commercial";

        final int currentCount = repository.findSuggested(department, areaOfWork, all).size();

        repository.save(createCourse("one", ImmutableSet.of("department:" + department)));
        repository.save(createCourse("two", ImmutableSet.of("department:" + department)));
        repository.save(createCourse("three", ImmutableSet.of("department:" + department)));
        repository.save(createCourse("four", ImmutableSet.of("area-of-work:" + areaOfWork)));
        repository.save(createCourse("five", ImmutableSet.of("area-of-work:" + areaOfWork)));

        Collection<Course> courses = repository.findSuggested(department, areaOfWork, all);

        assertThat(courses.size(), is(currentCount + 5));
    }

    @Test
    public void shouldFindAllSuggestedLearningExcludingMandatory() {

        final String department = "co";
        final String areaOfWork = "commercial";

        final int currentCount = repository.findSuggested(department, areaOfWork, all).size();

        repository.save(createCourse("one", ImmutableSet.of("department:" + department)));
        repository.save(createCourse("two", ImmutableSet.of("department:" + department)));
        repository.save(createCourse("three", ImmutableSet.of("mandatory:" + department, "department:" + department)));
        repository.save(createCourse("four", ImmutableSet.of("mandatory:" + department, "area-of-work:" + areaOfWork)));
        repository.save(createCourse("five", ImmutableSet.of("area-of-work:" + areaOfWork)));

        Collection<Course> courses = repository.findSuggested(department, areaOfWork, all);

        assertThat(courses.size(), is(currentCount + 3));
    }

    private Course createCourse() {
        return createCourse("title", emptySet());
    }

    private Course createCourse(String title, Set<String> tags) {
        return new Course(title, "shortDescription", "description",
                "learningOutcomes", 1000L, tags);
    }
}
