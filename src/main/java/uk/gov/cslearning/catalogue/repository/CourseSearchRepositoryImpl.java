package uk.gov.cslearning.catalogue.repository;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import org.springframework.stereotype.Repository;
import uk.gov.cslearning.catalogue.api.FilterParameters;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.SearchPage;

import static com.google.common.base.Preconditions.checkArgument;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;


@Repository
public class CourseSearchRepositoryImpl implements CourseSearchRepository {

    private static Logger LOGGER = LoggerFactory.getLogger(CourseSearchRepositoryImpl.class);

    private ElasticsearchOperations operations;

    public CourseSearchRepositoryImpl(ElasticsearchOperations operations) {
        checkArgument(operations != null);
        this.operations = operations;
    }

    @Override
    public SearchPage search(String query, Pageable pageable,FilterParameters filterParameters) {
        SearchPage searchPage = new SearchPage();

        Page<Course> coursePage = executeSearchQuery(query, pageable,filterParameters);

        searchPage.setCourses(coursePage);

        return searchPage;
    }

    public Page<Course> executeSearchQuery(String query, Pageable pageable, FilterParameters filterParameters) {

        BoolQueryBuilder boolQuery =  boolQuery().must(QueryBuilders.multiMatchQuery(query)
                .field("title", 8)
                .field("shortDescription", 4)
                .field("description", 2)
                .field("learningOutcomes", 2)
                .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                .fuzziness(Fuzziness.ONE)
                );

        if (filterParameters.getType() != null && !filterParameters.getType().equals("")) {
            // check if we have a number of types
            String[] types = filterParameters.getType().split(",");
            BoolQueryBuilder  filterQuery =QueryBuilders.boolQuery();

            for ( String type : types) { // should = OR but not restrictive
                filterQuery = filterQuery.should(QueryBuilders.matchQuery("modules.type", type));
            }
            filterQuery.minimumShouldMatch(1); // implies restriction
            boolQuery= boolQuery.must(filterQuery);
        }

        if (filterParameters.getCost() != null && !filterParameters.getCost().equals("")) {
            // only one possible value right now
                boolQuery= boolQuery
                        .must(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("modules.price", 0)));
        }


        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(
                    boolQuery
                )
                .withPageable(pageable)
                .build();

        return operations.queryForPage(searchQuery, Course.class);

    }
}
