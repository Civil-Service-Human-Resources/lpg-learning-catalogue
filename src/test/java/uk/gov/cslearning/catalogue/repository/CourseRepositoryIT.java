package uk.gov.cslearning.catalogue.repository;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.Audience;
import uk.gov.cslearning.catalogue.domain.module.FaceToFaceModule;
import uk.gov.cslearning.catalogue.domain.module.Module;

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

        final long currentCount = repository.findMandatory("co", all).getTotalElements();

        Course one = createCourse("one", "co", true, null);
        Course two = createCourse("two", "co", true, null);

        repository.save(one);
        repository.save(two);

        Page<Course> page = repository.findMandatory("co", all);

        assertThat(page.getTotalElements(), is(currentCount + 2));
        assertThat(page.getContent(), hasItem(one));
        assertThat(page.getContent(), hasItem(two));
    }

    @Test
    public void shouldFindMandatoryCoursesForAll() {

        final long currentCount = repository.findMandatory("co", all).getTotalElements();

        Course one = createCourse("one", "co", true, null);

        repository.save(one);

        Page<Course> page = repository.findMandatory("co", all);

        assertThat(page.getTotalElements(), is(currentCount + 1));
        assertThat(page.getContent(), hasItem(one));
    }

    @Test
    public void shouldNotFindMandatoryCoursesForOtherDepartment() {

        final long currentCount = repository.findMandatory("co", all).getTotalElements();

        Course one = createCourse("one", "hmrc", true, null);

        repository.save(one);

        Page<Course> page = repository.findMandatory("co", all);

        assertThat(page.getTotalElements(), is(currentCount));
    }

    @Test
    public void shouldFindSuggestedLearningByAreaOfWork() {

        final String department = "co";
        final String areaOfWork = "commercial";

        final long currentCount = repository.findSuggested(department, areaOfWork, all).getTotalElements();

        repository.save(createCourse("one", null, false, areaOfWork));
        repository.save(createCourse("two", null, false, areaOfWork));
        repository.save(createCourse("three",null, false, areaOfWork));

        Page<Course> page = repository.findSuggested(department, areaOfWork, all);

        assertThat(page.getTotalElements(), is(currentCount + 3));
    }

    @Test
    public void shouldFindSuggestedLearningByDepartment() {

        final String department = "co";
        final String areaOfWork = "commercial";

        final long currentCount = repository.findSuggested(department, areaOfWork, all).getTotalElements();

        repository.save(createCourse("one", department, false, null));
        repository.save(createCourse("two", department, false, null));
        repository.save(createCourse("three", department, false, null));

        Page<Course> page = repository.findSuggested(department, areaOfWork, all);

        assertThat(page.getTotalElements(), is(currentCount + 3));
    }

    @Test
    public void shouldFindSuggestedLearningByAreaOfWorkOrDepartment() {

        final String department = "co";
        final String areaOfWork = "commercial";

        final long currentCount = repository.findSuggested(department, areaOfWork, all).getTotalElements();

        repository.save(createCourse("one", department, false, null));
        repository.save(createCourse("two", department, false, null));
        repository.save(createCourse("three", department, false, null));
        repository.save(createCourse("four", null, false, areaOfWork));
        repository.save(createCourse("five", null, false, areaOfWork));

        Page<Course> page = repository.findSuggested(department, areaOfWork, all);

        assertThat(page.getTotalElements(), is(currentCount + 5));
    }

    @Test
    public void shouldFindAllSuggestedLearningExcludingMandatory() {

        final String department = "co";
        final String areaOfWork = "commercial";

        final long currentCount = repository.findSuggested(department, areaOfWork, all).getTotalElements();

        repository.save(createCourse("one", department, false, null));
        repository.save(createCourse("two", department, false, null));
        repository.save(createCourse("three", department, true, null));
        repository.save(createCourse("four", department, true, areaOfWork));
        repository.save(createCourse("five", null, false, areaOfWork));

        Page<Course> page = repository.findSuggested(department, areaOfWork, all);

        assertThat(page.getTotalElements(), is(currentCount + 3));
    }

    private Course createCourse() {
        return createCourse("title", null, false, null);
    }

    private Course createCourse(String title, String department, Boolean mandatory, String areaOfWork) {
        Course course = new Course(title, "shortDescription", "description",
                "learningOutcomes");

        Audience audience = new Audience();
        if (department != null) {
            audience.setDepartments(ImmutableSet.of(department));
        }
        if (areaOfWork != null) {
            audience.setAreasOfWork(ImmutableSet.of(areaOfWork));
        }
        audience.setMandatory(mandatory);

        Module module = new FaceToFaceModule("productCode");
        module.addAudience(audience);

        course.setModules(ImmutableList.of(module));

        return course;
    }
}
