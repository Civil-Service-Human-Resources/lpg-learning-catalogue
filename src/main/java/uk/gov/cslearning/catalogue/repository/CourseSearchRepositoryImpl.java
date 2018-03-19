package uk.gov.cslearning.catalogue.repository;

import org.elasticsearch.action.search.SearchResponse;
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
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

@Repository
public class CourseSearchRepositoryImpl implements CourseSearchRepository {

    private static Logger LOGGER = LoggerFactory.getLogger(CourseSearchRepositoryImpl.class);

    private ElasticsearchTemplate template;

    public CourseSearchRepositoryImpl(ElasticsearchTemplate template) {
        checkArgument(template != null);
        this.template = template;
    }

    @Override
    public List<Course> search(String query) {
        LOGGER.info("Executing search query for " + query);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(termQuery("title", query))
                .withQuery(matchQuery("description", query))
                .withQuery(matchQuery("learningOutcomes", query))
                .build();
        List<Course> courses = template.queryForList(searchQuery, Course.class);
        return courses;
    }

    @Override
    public SearchPage suggestions (String suggestText) {
        LOGGER.info("Executing suggestions query for " + suggestText);
        SearchPage searchPage = new SearchPage();

        SuggestionBuilder titleSuggestionBuilder =
                SuggestBuilders.phraseSuggestion("title").text(suggestText);

        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.setGlobalText(suggestText);
        suggestBuilder.addSuggestion("suggest_title", titleSuggestionBuilder);

        SearchResponse searchResponse = template.suggest(suggestBuilder, Course.class);
        Suggest suggest = searchResponse.getSuggest();

        Suggestion suggestion =  suggest.getSuggestion("suggest_title");
        List<Entry> list = suggestion.getEntries();
        for (int i = 0; i < list.size(); i++){
            List<Option> optionList = list.get(i).getOptions();
            for (int j = 0; j < optionList.size(); j++){
                if(searchPage.getTopScoringSuggestion() == null || optionList.get(j).getScore() > searchPage.getTopScoringSuggestion().getScore()){
                    searchPage.setTopScoringSuggestion(optionList.get(j));
                }
            }
        }


        // String suggested = loop and return highest
//        List<Course> courses = template.queryForList(suggestText, Course.class);

        // return list of courses and suggested text as paginated
        // create new object expends page (search page)
        // return searchPage(suggested, pageOfResults)
        List<Course> courseList= new LinkedList<>();
        Course course = new Course();
        course.setTitle("Test");
        courseList.add(course);
        Page<Course> coursePage = new PageImpl<>(courseList);
        searchPage.setCourses(coursePage);

        return searchPage;
    }
}
