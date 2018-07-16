package uk.gov.cslearning.catalogue.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cslearning.catalogue.api.FilterParameters;
import uk.gov.cslearning.catalogue.api.PageParameters;
import uk.gov.cslearning.catalogue.domain.Resource;
import uk.gov.cslearning.catalogue.domain.SearchPage;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * UserRepository integration test.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class ResourceSearchRepositoryIT {

    private PageRequest all = PageRequest.of(0, 1000);

    @Autowired
    private ResourceRepository repository;

    @Test
    public void shouldReturnAccurateSuggestionAndResourceWithMisspelledSearchQuery() {
        PageParameters pageParameters = new PageParameters();
        FilterParameters filterParameters = new FilterParameters();
        Pageable pageable = pageParameters.getPageRequest();

        SearchPage actualSearchPage = repository.search("Wirking with Budgets", pageable,filterParameters);

        String actualSuggestionText = actualSearchPage.getTopScoringSuggestion().getText().toString();
        Page<Resource> resourcePage = actualSearchPage.getResources();
        List<Resource> resourceList = resourcePage.getContent();
        String actualTitle = resourceList.get(0).getTitle();

        assertThat(actualSuggestionText, is("working with budgets"));
        assertThat(actualTitle, is("Working with budgets"));
    }

    @Test
    public void shouldReturnCorrectPageForSearchQuery() {
        PageParameters pageParameters = new PageParameters();
        Pageable pageable = pageParameters.getPageRequest();
        FilterParameters filterParameters = new FilterParameters();

        SearchPage actualSearchPage = repository.search("Budgets", pageable,filterParameters);
        List<Resource> actualResources = actualSearchPage.getResources().getContent();

        assertThat(actualResources.size(), is(4));
        assertThat(actualResources.get(0).getTitle(), is("Working with budgets"));
        assertThat(actualResources.get(0).getId(), is("BUfZwRaWQrKAhSSjlJ7lCg"));
        assertThat(actualResources.get(0).getShortDescription(), is("This topic introduces you to the fundamental principles of budget management and governance processes. "));
    }
    
    @Test
    public void shouldReturnCorrectPageForSearchQueryWithMissingField() {
        PageParameters pageParameters = new PageParameters();
        Pageable pageable = pageParameters.getPageRequest();
        FilterParameters filterParameters = new FilterParameters();

        SearchPage actualSearchPage = repository.search("Spotify engineering culture: part 1", pageable, filterParameters );
        List<Resource> actualResources = actualSearchPage.getResources().getContent();

        assertThat(actualResources.get(0).getTitle(), is("Spotify engineering culture: part 1"));
        assertThat(actualResources.get(0).getLearningOutcomes(), is(""));
    }

    @Test
    public void shouldReturnFilteredResultsCorrectlyForType() {
        PageParameters pageParameters = new PageParameters();
        Pageable pageable = pageParameters.getPageRequest();
        FilterParameters filterParameters = new FilterParameters();
        filterParameters.setTypes(asList("face to face"));

        SearchPage actualSearchPage = repository.search("why", pageable, filterParameters );
        List<Resource> actualResources = actualSearchPage.getResources().getContent();

        assertThat(actualResources.get(0).getTitle(), is("Understanding and using business cases"));
    }
}
