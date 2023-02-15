package uk.gov.cslearning.catalogue.repository;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
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
import static org.elasticsearch.index.query.QueryBuilders.*;

@Repository
public class CourseSuggestionsRepositoryImpl implements CourseSuggestionsRepository {
    private ElasticsearchOperations operations;

    public CourseSuggestionsRepositoryImpl(ElasticsearchOperations operations) {
        checkArgument(operations != null);
        this.operations = operations;
    }

    // @Override
    // public Page<Course> findSuggested(GetCoursesParameters parameters, Pageable pageable) {
    //     BoolQueryBuilder boolQuery = boolQuery();

    //     parameters.getDepartments().forEach(s -> boolQuery.should(QueryBuilders.matchPhraseQuery("audiences.departments", s)));
    //     boolQuery.should(QueryBuilders.matchPhraseQuery("audiences.areasOfWork", parameters.getAreaOfWork()));
    //     boolQuery.should(QueryBuilders.matchPhraseQuery("audiences.interests", parameters.getInterest()));

    //     BoolQueryBuilder filterQuery = boolQuery();
    //     filterQuery.must(QueryBuilders.matchQuery("audiences.grades", parameters.getGrade()));
    //     filterQuery.must(QueryBuilders.matchQuery("status", parameters.getStatus()));
    //     filterQuery.mustNot(QueryBuilders.matchQuery("audiences.type", "REQUIRED_LEARNING"));

    //     parameters.getExcludeAreasOfWork().forEach(aow -> filterQuery.mustNot(QueryBuilders.matchPhraseQuery("audiences.areasOfWork", aow)));
    //     parameters.getExcludeInterests().forEach(interest -> filterQuery.mustNot(QueryBuilders.matchPhraseQuery("audiences.interests", interest)));
    //     parameters.getExcludeDepartments().forEach(department -> filterQuery.mustNot(QueryBuilders.matchPhraseQuery("audiences.departments", department)));

    //     Query searchQuery = new NativeSearchQueryBuilder()
    //             .withQuery(boolQuery)
    //             .withFilter(filterQuery)
    //             .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
    //             .withPageable(pageable)
    //             .build();

    //     return Utils.searchPageToPage(operations.search(searchQuery, Course.class), pageable);
    // }

    // NEW!
    @Override
    public Page<Course> findSuggested(GetCoursesParameters parameters, Pageable pageable) {
        System.out.println("-- PARAMETERS 1 -- ");
        System.out.println("Status: " + parameters.getStatus());
        System.out.println("Departments size: " + parameters.getDepartments().size());
        System.out.println("Area of work: " + parameters.getAreaOfWork());
        System.out.println("Interest: " + parameters.getInterest());
        System.out.println("Grade: " + parameters.getGrade());

        BoolQueryBuilder courseQuery = boolQuery();
        courseQuery.must(matchQuery("status", parameters.getStatus()));
        parameters.getDepartments().forEach(s -> courseQuery.must(QueryBuilders.matchPhraseQuery("audiences.departments", s)));

        BoolQueryBuilder query = boolQuery().must(matchQuery("audiences.type", "OPEN"));
        parameters.getDepartments().forEach(s -> query.must(QueryBuilders.matchPhraseQuery("audiences.departments", s)));
        if(!parameters.getAreaOfWork().equals("NONE")) query.must(matchQuery("audiences.areasOfWork", parameters.getAreaOfWork()));
        if(!parameters.getInterest().equals("NONE")) query.must(matchQuery("audiences.interests", parameters.getInterest()));
        if(!parameters.getGrade().equals("NONE")) query.must(matchQuery("audiences.grades", parameters.getGrade()));

        parameters.getExcludeAreasOfWork().forEach(aow -> query.mustNot(QueryBuilders.matchPhraseQuery("audiences.areasOfWork", aow)));
        parameters.getExcludeInterests().forEach(interest -> query.mustNot(QueryBuilders.matchPhraseQuery("audiences.interests", interest)));
        parameters.getExcludeDepartments().forEach(department -> query.mustNot(QueryBuilders.matchPhraseQuery("audiences.departments", department)));

        NestedQueryBuilder audiencesNestedQuery = nestedQuery("audiences", query, ScoreMode.Avg);
        courseQuery.must(audiencesNestedQuery);

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(courseQuery)
                .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .withPageable(pageable)
                .build();

        return Utils.searchPageToPage(operations.search(searchQuery, Course.class), pageable);

    }

    @Override
    public Page<Course> findSuggested(List<String> departmentList, String areaOfWork, String interest, String status, String grade, Pageable pageable) {
        BoolQueryBuilder courseQuery = getCourseQuery(status, departmentList, areaOfWork, interest, grade);

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(courseQuery)
                .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .withPageable(pageable)
                .build();

        return Utils.searchPageToPage(operations.search(searchQuery, Course.class), pageable);
    }

    private BoolQueryBuilder getCourseQuery(String status, List<String> departments, String areaOfWork, String interest, String grade){
        BoolQueryBuilder courseQuery = boolQuery();
        courseQuery.must(matchQuery("status", status));

        NestedQueryBuilder audiencesNestedQuery = getAudienceNestedQuery(departments, areaOfWork, interest, grade);
        courseQuery.must(audiencesNestedQuery);

        return courseQuery;
    }

    private NestedQueryBuilder getAudienceNestedQuery(List<String> departments, String areaOfWork, String interest, String grade){
        BoolQueryBuilder query = boolQuery().must(matchQuery("audiences.type", "OPEN"));
        departments.forEach(s -> query.must(QueryBuilders.matchPhraseQuery("audiences.departments", s)));

        if(!areaOfWork.equals("NONE")) query.must(matchQuery("audiences.areasOfWork", areaOfWork));
        if(!interest.equals("NONE")) query.must(matchQuery("audiences.interests", interest));
        if(!grade.equals("NONE")) query.must(matchQuery("audiences.grades", grade));

        return nestedQuery("audiences", query, ScoreMode.Avg);
    }
}
