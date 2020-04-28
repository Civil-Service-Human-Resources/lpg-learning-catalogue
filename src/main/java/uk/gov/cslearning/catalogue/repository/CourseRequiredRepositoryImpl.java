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
import static org.elasticsearch.search.sort.SortBuilders.fieldSort;
import static org.elasticsearch.search.sort.SortOrder.ASC;

@Repository
public class CourseRequiredRepositoryImpl implements CourseRequiredRepository {
    private ElasticsearchOperations operations;

    public CourseRequiredRepositoryImpl(ElasticsearchOperations operations) {
        checkArgument(operations != null);
        this.operations = operations;
    }

    @Override
    public Page<Course> findRequired(String profession, String gradeCode, List<String>departments, List<String>otherAreasOfWork,  List<String>interests, String courseStatus,  Pageable pageable){

        BoolQueryBuilder boolQuery = boolQuery();

        // Check if departments is null, if so dont do this.
        departments.forEach(s -> boolQuery.should(QueryBuilders.matchQuery("audiences.departments.keyword", s)));
        otherAreasOfWork.forEach(o -> boolQuery.should(QueryBuilders.matchQuery("audiences.areasOfWork.keyword", o)));
        interests.forEach(i -> boolQuery.should(QueryBuilders.matchQuery("audiences.interests.keyword", i)));

        boolQuery.should(QueryBuilders.matchQuery("audiences.areasOfWork.keyword", profession));
        boolQuery.should(QueryBuilders.matchQuery("audiences.grades.keyword", gradeCode));

        BoolQueryBuilder filterQuery = boolQuery();

        filterQuery.must(QueryBuilders.matchQuery("status", courseStatus));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withFilter(filterQuery)
                .withSort(SortBuilders.scoreSort())
                //.withSort(fieldSort("_id").order(ASC))
                .withPageable(pageable)
                .build();

        return operations.queryForPage(searchQuery, Course.class);
    }
}

