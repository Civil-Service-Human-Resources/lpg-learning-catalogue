package uk.gov.cslearning.catalogue.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cslearning.catalogue.api.FilterParameters;
import uk.gov.cslearning.catalogue.api.OwnerParameters;
import uk.gov.cslearning.catalogue.api.PageParameters;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.SearchPage;
import uk.gov.cslearning.catalogue.domain.Status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * UserRepository integration shouldAddCancellationPolicyToLearningProvider.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class CourseSearchRepositoryIT {

    @Autowired
    private CourseRepository repository;

    @Test
    public void shouldReturnAccurateSuggestionAndResourceWithMisspelledSearchQuery() {
        PageParameters pageParameters = new PageParameters();
        FilterParameters filterParameters = new FilterParameters();
        Pageable pageable = pageParameters.getPageRequest();
        Collection<Status> statusCollection = new ArrayList();
        OwnerParameters ownerParameters = new OwnerParameters();

        statusCollection.add(Status.PUBLISHED);

        SearchPage actualSearchPage = repository.search("Wirking with Budgets", pageable, filterParameters, statusCollection, ownerParameters);

        String actualSuggestionText = actualSearchPage.getTopScoringSuggestion().getText().toString();
        Page<Course> resourcePage = actualSearchPage.getCourses();
        List<Course> resourceList = resourcePage.getContent();
        String actualTitle = resourceList.get(0).getTitle();

        assertThat(actualSuggestionText, is("working with budgets"));
        assertThat(actualTitle, is("Working with budgets"));
    }

    @Test
    public void shouldReturnCorrectPageForSearchQuery() {
        PageParameters pageParameters = new PageParameters();
        Pageable pageable = pageParameters.getPageRequest();
        FilterParameters filterParameters = new FilterParameters();
        Collection<Status> statusCollection = new ArrayList();
        OwnerParameters ownerParameters = new OwnerParameters();

        statusCollection.add(Status.PUBLISHED);

        SearchPage actualSearchPage = repository.search("Budgets", pageable, filterParameters, statusCollection, ownerParameters);
        List<Course> actualResources = actualSearchPage.getCourses().getContent();

        assertThat(actualResources.size(), is(2));
        assertThat(actualResources.get(0).getTitle(), is("Working with budgets"));
        assertThat(actualResources.get(0).getShortDescription(), is("This topic introduces you to the fundamental principles of budget management and governance processes. "));
    }

    @Test
    public void shouldReturnCorrectPageForSearchQueryWithMissingField() {
        PageParameters pageParameters = new PageParameters();
        Pageable pageable = pageParameters.getPageRequest();
        FilterParameters filterParameters = new FilterParameters();
        Collection<Status> statusCollection = new ArrayList();
        OwnerParameters ownerParameters = new OwnerParameters();

        statusCollection.add(Status.PUBLISHED);

        SearchPage actualSearchPage = repository.search("Spotify engineering culture: part 1", pageable, filterParameters, statusCollection, ownerParameters);
        List<Course> actualResources = actualSearchPage.getCourses().getContent();

        assertThat(actualResources.get(0).getTitle(), is("Spotify engineering culture: part 1"));
        assertThat(actualResources.get(0).getLearningOutcomes(), is(""));
    }

    @Test
    public void shouldReturnFilteredResultsCorrectlyForType() {
        PageParameters pageParameters = new PageParameters();
        Pageable pageable = pageParameters.getPageRequest();
        FilterParameters filterParameters = new FilterParameters();
        filterParameters.setTypes(asList("face to face"));
        Collection<Status> statusCollection = new ArrayList();
        OwnerParameters ownerParameters = new OwnerParameters();
        statusCollection.add(Status.PUBLISHED);

        SearchPage actualSearchPage = repository.search("why", pageable, filterParameters, statusCollection, ownerParameters);
        List<Course> actualResources = actualSearchPage.getCourses().getContent();

        assertThat(actualResources.get(0).getTitle(), is("Understanding and using business cases"));
    }
}
