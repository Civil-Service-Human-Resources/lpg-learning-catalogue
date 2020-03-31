package uk.gov.cslearning.catalogue.repository;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.catalogue.domain.Course;

import static com.google.common.base.Preconditions.checkArgument;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;

@Repository
public class CourseRepositoryImpl {
    private ElasticsearchOperations operations;

    public CourseRepositoryImpl(ElasticsearchOperations operations) {
        checkArgument(operations != null);
        this.operations = operations;
    }

    public Page<Course> findPublishedAndArchivedMandatoryCourses() {
        BoolQueryBuilder boolQuery = boolQuery();
        boolQuery.should(QueryBuilders.matchPhraseQuery("status", "Published"));
        boolQuery.should(QueryBuilders.matchPhraseQuery("status", "Archived"));

        BoolQueryBuilder filterQuery = boolQuery();
        filterQuery.must(QueryBuilders.matchQuery("audiences.type", "REQUIRED_LEARNING"));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withFilter(filterQuery)
                .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .build();

        return operations.queryForPage(searchQuery, Course.class);
    }
}