package uk.gov.cslearning.catalogue.repository;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.Suggest.Suggestion;
import org.elasticsearch.search.suggest.Suggest.Suggestion.Entry.Option;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestion.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.SearchPage;

import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;


@Repository
public class CourseSearchRepositoryImpl implements CourseSearchRepository {

    private static Logger LOGGER = LoggerFactory.getLogger(CourseSearchRepositoryImpl.class);

    private ElasticsearchTemplate template;

    public CourseSearchRepositoryImpl(ElasticsearchTemplate template) {
        checkArgument(template != null);
        this.template = template;
    }

    @Override
    public SearchPage search(String query) {
        LOGGER.info("Executing suggestions query for {}", query);
        SearchPage searchPage = new SearchPage();

        SuggestionBuilder titleSuggestionBuilder =
                SuggestBuilders.phraseSuggestion("title").text(query);
        SuggestionBuilder shortDescriptionSuggestionBuilder =
                SuggestBuilders.phraseSuggestion("shortDescription").text(query);
        SuggestionBuilder learningOutcomesSuggestionBuilder =
                SuggestBuilders.phraseSuggestion("learningOutcome s").text(query);

        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.setGlobalText(query);
        suggestBuilder.addSuggestion("suggestTitle", titleSuggestionBuilder);
        suggestBuilder.addSuggestion("suggestShortDesc", shortDescriptionSuggestionBuilder);
        suggestBuilder.addSuggestion("suggestLearningOutcomes", learningOutcomesSuggestionBuilder);

        SearchResponse searchResponse = template.suggest(suggestBuilder, Course.class);

        Suggest suggest = searchResponse.getSuggest();
        Suggestion titleSuggestion = suggest.getSuggestion("suggestTitle");
        Suggestion shortDescSuggestion = suggest.getSuggestion("suggestShortDesc");
        Suggestion suggestionLearningOutcomes = suggest.getSuggestion("suggestLearningOutcomes");

        List<Entry> list = new LinkedList<>();
        list.addAll(titleSuggestion.getEntries());
        list.addAll(shortDescSuggestion.getEntries());
        list.addAll(suggestionLearningOutcomes.getEntries());

        for (int i = 0; i < list.size(); i++) {
            List<Option> optionList = list.get(i).getOptions();
            for (int j = 0; j < optionList.size(); j++) {
                if (searchPage.getTopScoringSuggestion() == null || optionList.get(j).getScore() > searchPage.getTopScoringSuggestion().getScore()) {
                    searchPage.setTopScoringSuggestion(optionList.get(j));
                }
            }
        }

        List<Course> courseList = executeSearchQuery(query);
        Page<Course> coursePage = new PageImpl<>(courseList);
        searchPage.setCourses(coursePage);

        if (searchPage.getTopScoringSuggestion() != null) {
            String message = searchPage.getTopScoringSuggestion().getText().toString();
            LOGGER.info("Top scoring suggestion is: {}", message);
        }

        return searchPage;
    }

    public List<Course> executeSearchQuery (String query) {
        LOGGER.info("Executing search query for {}", query);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(query)
                        .field("title")
                        .field("shortDescription")
                        .field("learningOutcomes")
                        .type(MultiMatchQueryBuilder.Type.BEST_FIELDS))
                .build();

        return template.queryForList(searchQuery, Course.class);
    }
}
