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
