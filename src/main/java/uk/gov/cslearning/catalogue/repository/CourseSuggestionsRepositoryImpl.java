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

import java.util.List;

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
    public Page<Course> findSuggested(List<String> departmentList, String areaOfWork, String interest, String status, String grade, Pageable pageable) {
        BoolQueryBuilder boolQuery = boolQuery();

        departmentList.forEach(s -> boolQuery.should(QueryBuilders.matchQuery("audiences.departments.keyword", s)));
        boolQuery.should(QueryBuilders.matchQuery("audiences.areasOfWork.keyword", areaOfWork));
        boolQuery.should(QueryBuilders.matchQuery("audiences.interests.keyword", interest));

        BoolQueryBuilder filterQuery = boolQuery();
        filterQuery.must(QueryBuilders.matchQuery("audiences.grades.keyword", grade));
        filterQuery.must(QueryBuilders.matchQuery("status", status));
        filterQuery.must(QueryBuilders.matchQuery("audiences.type", "OPEN"));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withFilter(filterQuery)
                .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .withPageable(pageable)
                .build();

        return operations.queryForPage(searchQuery, Course.class);
    }
}