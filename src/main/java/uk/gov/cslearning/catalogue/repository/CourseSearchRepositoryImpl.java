package uk.gov.cslearning.catalogue.repository;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.Fuzziness;
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
import java.util.Map;

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
        SuggestBuilder suggestBuilder = getSuggestBuilder(query);

        SearchResponse searchResponse = template.suggest(suggestBuilder, Course.class);

        List<Entry> suggestionList = getSuggestionListFromSearchResponse(suggestBuilder, searchResponse);

        SearchPage searchPage = new SearchPage();
        setTopSuggestedOption(suggestionList, searchPage);

        List<Course> courseList = executeSearchQuery(query);
        Page<Course> coursePage = new PageImpl<>(courseList);
        searchPage.setCourses(coursePage);

        if (searchPage.getTopScoringSuggestion() != null) {
            String message = searchPage.getTopScoringSuggestion().getText().toString();
            LOGGER.info("Top scoring suggestion is: {}", message);
        }

        return searchPage;
    }

    private List<Entry> getSuggestionListFromSearchResponse(SuggestBuilder suggestBuilder, SearchResponse searchResponse) {
        Suggest suggest = searchResponse.getSuggest();

        List<Entry> suggestionList = new LinkedList<>();
        Map<String, SuggestionBuilder<?>> suggestions = suggestBuilder.getSuggestions();
        for (Map.Entry<String, SuggestionBuilder<?>> entry : suggestions.entrySet()) {
            Suggestion suggestion = suggest.getSuggestion(entry.getKey());
            suggestionList.addAll(suggestion.getEntries());
        }
        return suggestionList;
    }

    private void setTopSuggestedOption(List<Entry> suggestionList, SearchPage searchPage) {
        for (int i = 0; i < suggestionList.size(); i++) {
            List<Option> optionList = suggestionList.get(i).getOptions();
            for (int j = 0; j < optionList.size(); j++) {
                String suggestionResult = optionList.get(j).getText().toString();
                LOGGER.info("Suggestion: {}", suggestionResult);
                if (searchPage.getTopScoringSuggestion() == null || optionList.get(j).getScore() > searchPage.getTopScoringSuggestion().getScore()) {
                    searchPage.setTopScoringSuggestion(optionList.get(j));
                }
            }
        }
    }

    private SuggestBuilder getSuggestBuilder(String query) {
        SuggestionBuilder titleSuggestionBuilder =
                SuggestBuilders.phraseSuggestion("title").text(query);
        SuggestionBuilder shortDescriptionSuggestionBuilder =
                SuggestBuilders.phraseSuggestion("shortDescription").text(query);
        SuggestionBuilder learningOutcomesSuggestionBuilder =
                SuggestBuilders.phraseSuggestion("learningOutcomes").text(query);

        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.setGlobalText(query);
        suggestBuilder.addSuggestion("suggestTitle", titleSuggestionBuilder);
        suggestBuilder.addSuggestion("suggestShortDesc", shortDescriptionSuggestionBuilder);
        suggestBuilder.addSuggestion("suggestLearningOutcomes", learningOutcomesSuggestionBuilder);

        return suggestBuilder;
    }

    public List<Course> executeSearchQuery(String query) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(query)
                        .field("title", 8)
                        .field("shortDescription", 4)
                        .field("description", 2)
                        .field("learningOutcomes", 2)
                        .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                        .fuzziness(Fuzziness.ONE)
                )
                .build();

        return template.queryForList(searchQuery, Course.class);
    }
}
