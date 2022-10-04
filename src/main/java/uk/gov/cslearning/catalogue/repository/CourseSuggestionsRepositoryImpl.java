package uk.gov.cslearning.catalogue.repository;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.catalogue.api.v2.model.GetCoursesParameters;
import uk.gov.cslearning.catalogue.Utils;
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
    public Page<Course> findSuggested(GetCoursesParameters parameters, Pageable pageable) {
        BoolQueryBuilder boolQuery = boolQuery();

        parameters.getDepartments().forEach(s -> boolQuery.should(QueryBuilders.matchPhraseQuery("audiences.departments", s)));
        boolQuery.should(QueryBuilders.matchPhraseQuery("audiences.areasOfWork", parameters.getAreaOfWork()));
        boolQuery.should(QueryBuilders.matchPhraseQuery("audiences.interests", parameters.getInterest()));

        BoolQueryBuilder filterQuery = boolQuery();
        filterQuery.must(QueryBuilders.matchQuery("audiences.grades", parameters.getGrade()));
        filterQuery.must(QueryBuilders.matchQuery("status", parameters.getStatus()));
        filterQuery.mustNot(QueryBuilders.matchQuery("audiences.type", "REQUIRED_LEARNING"));

        parameters.getExcludeAreasOfWork().forEach(aow -> filterQuery.mustNot(QueryBuilders.matchPhraseQuery("audiences.areasOfWork", aow)));
        parameters.getExcludeInterests().forEach(interest -> filterQuery.mustNot(QueryBuilders.matchPhraseQuery("audiences.interests", interest)));
        parameters.getExcludeDepartments().forEach(department -> filterQuery.mustNot(QueryBuilders.matchPhraseQuery("audiences.departments", department)));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withFilter(filterQuery)
                .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .withPageable(pageable)
                .build();

        return operations.queryForPage(searchQuery, Course.class);
    }

    @Override
    public Page<Course> findSuggested(List<String> departmentList, String areaOfWork, String interest, String status, String grade, Pageable pageable) {
        BoolQueryBuilder boolQuery = boolQuery();

        departmentList.forEach(s -> boolQuery.should(QueryBuilders.matchPhraseQuery("audiences.departments", s)));
        boolQuery.should(QueryBuilders.matchPhraseQuery("audiences.areasOfWork", areaOfWork));
        boolQuery.should(QueryBuilders.matchPhraseQuery("audiences.interests", interest));

        BoolQueryBuilder filterQuery = boolQuery();
        filterQuery.must(QueryBuilders.matchQuery("audiences.grades", grade));
        filterQuery.must(QueryBuilders.matchQuery("status", status));
        filterQuery.mustNot(QueryBuilders.matchQuery("audiences.type", "REQUIRED_LEARNING"));

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withFilter(filterQuery)
                .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .withPageable(pageable)
                .build();

        return Utils.searchPageToPage(operations.search(searchQuery, Course.class), pageable);
    }
}
