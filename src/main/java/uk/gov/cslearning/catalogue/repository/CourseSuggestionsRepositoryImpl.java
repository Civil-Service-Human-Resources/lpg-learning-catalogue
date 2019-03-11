package uk.gov.cslearning.catalogue.repository;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.catalogue.domain.Course;

import static com.google.common.base.Preconditions.checkArgument;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;

@Repository
public class CourseSuggestionsRepositoryImpl implements CourseSuggestionsRepository {
    private ElasticsearchOperations operations;

    public CourseSuggestionsRepositoryImpl(ElasticsearchOperations operations) {
        checkArgument(operations != null);
        this.operations = operations;
    }

    @Override
    public Page<Course> findSuggested(String department, String areaOfWork, String interest, String status, Pageable pageable) {
        BoolQueryBuilder boolQuery = boolQuery();

        boolQuery.should(QueryBuilders.matchQuery("audiences.departments", department));
        boolQuery.should(QueryBuilders.matchQuery("audiences.areasOfWork", areaOfWork));
        boolQuery.should(QueryBuilders.matchQuery("audiences.interests", interest));

        BoolQueryBuilder filterQuery = boolQuery();
        filterQuery.mustNot(QueryBuilders.matchQuery("audiences.type", "REQUIRED_LEARNING"));
        filterQuery.minimumShouldMatch(1);

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withFilter(filterQuery)
                .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .withPageable(pageable)
                .build();

        return operations.queryForPage(searchQuery, Course.class);
    }
}