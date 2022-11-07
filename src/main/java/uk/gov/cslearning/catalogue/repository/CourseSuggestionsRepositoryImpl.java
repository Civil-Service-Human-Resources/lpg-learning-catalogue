package uk.gov.cslearning.catalogue.repository;

import org.apache.lucene.search.join.ScoreMode;
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
import uk.gov.cslearning.catalogue.api.v2.model.GetCoursesParameters;
import uk.gov.cslearning.catalogue.domain.Course;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.elasticsearch.index.query.QueryBuilders.*;

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
        BoolQueryBuilder courseQuery = boolQuery();
        courseQuery.must(matchQuery("status", status));

        BoolQueryBuilder audiencesQuery = boolQuery().must(matchQuery("audiences.type", "OPEN"));
        departmentList.forEach(s -> audiencesQuery.must(QueryBuilders.matchPhraseQuery("audiences.departments", s)));

        if(!areaOfWork.equals("NONE")) audiencesQuery.must(matchQuery("audiences.areasOfWork", areaOfWork));
        if(!interest.equals("NONE")) audiencesQuery.must(matchQuery("audiences.interests", interest));
        if(!interest.equals("NONE")) audiencesQuery.must(matchQuery("audiences.grades", grade));

        courseQuery.must(nestedQuery("audiences", audiencesQuery, ScoreMode.Avg));

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(courseQuery)
                .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .withPageable(pageable)
                .build();

        return operations.queryForPage(searchQuery, Course.class);
    }
}
