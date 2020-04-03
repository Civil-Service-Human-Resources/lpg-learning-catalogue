package uk.gov.cslearning.catalogue.repository;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.catalogue.domain.Course;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static uk.gov.cslearning.catalogue.domain.CourseType.REQUIRED_LEARNING;
import static uk.gov.cslearning.catalogue.domain.Status.ARCHIVED;
import static uk.gov.cslearning.catalogue.domain.Status.PUBLISHED;

@Repository
public class CourseRepositoryImpl {

    private ElasticsearchOperations operations;

    public CourseRepositoryImpl(ElasticsearchOperations operations) {
        checkArgument(operations != null);
        this.operations = operations;
    }

    public List<Course> findPublishedAndArchivedMandatoryCourses(Pageable pageable) {

        BoolQueryBuilder boolQuery = boolQuery();
        boolQuery.should(QueryBuilders.matchQuery("status", PUBLISHED));
        boolQuery.should(QueryBuilders.matchQuery("status", ARCHIVED));

        BoolQueryBuilder filterQuery = boolQuery();
        filterQuery.must(QueryBuilders.matchQuery("audiences.type", REQUIRED_LEARNING));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withFilter(filterQuery)
                .withPageable(pageable)
                .build();

        return operations.queryForList(searchQuery, Course.class);
    }
}