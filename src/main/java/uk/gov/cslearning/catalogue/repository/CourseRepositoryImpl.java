package uk.gov.cslearning.catalogue.repository;

import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.search.MatchQuery;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.gov.cslearning.catalogue.domain.Course;

import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;

@Repository
public class CourseRepositoryImpl implements CustomCourseRepository {
    private ElasticsearchOperations operations;

    public CourseRepositoryImpl(ElasticsearchOperations operations) {
        checkArgument(operations != null);
        this.operations = operations;
    }

    @Override
    public Page<Course> findSuggested(String department, String areaOfWork, String interest, String status, Pageable pageable){
        BoolQueryBuilder boolQuery = boolQuery();

        boolQuery.should(QueryBuilders.matchQuery("audiences.department", department));
        boolQuery.should(QueryBuilders.matchQuery("audiences.areasOfWork", areaOfWork));
        boolQuery.should(QueryBuilders.matchQuery("audiences.interests", interest));

//        boolQuery.must(QueryBuilders.matchQuery("status", status).zeroTermsQuery(MatchQuery.ZeroTermsQuery.NONE).operator(Operator.AND).fuzziness(Fuzziness.ZERO));
//        boolQuery.must(QueryBuilders.matchQuery("audiences.type", "OPEN").zeroTermsQuery(MatchQuery.ZeroTermsQuery.NONE).operator(Operator.AND).fuzziness(Fuzziness.ZERO));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .withPageable(pageable)
                .build();

        return operations.queryForPage(searchQuery, Course.class);
    }

    private BoolQueryBuilder addFilter(BoolQueryBuilder boolQuery, List<String> values, String key) {
        if (values != null && !values.isEmpty()) {
            BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();

            for (String department : values) {
                filterQuery = filterQuery
                        .should(QueryBuilders.matchQuery(key, department));
            }
            filterQuery.minimumShouldMatch(1);
            return boolQuery.must(filterQuery);
        }
        return boolQuery;
    }
}
