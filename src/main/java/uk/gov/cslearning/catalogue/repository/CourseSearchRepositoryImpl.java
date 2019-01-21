package uk.gov.cslearning.catalogue.repository;

import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.catalogue.api.FilterParameters;
import uk.gov.cslearning.catalogue.api.ProfileParameters;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.SearchPage;
import uk.gov.cslearning.catalogue.domain.Status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;

@Repository
public class CourseSearchRepositoryImpl implements CourseSearchRepository {

    private ElasticsearchOperations operations;

    public CourseSearchRepositoryImpl(ElasticsearchOperations operations) {
        checkArgument(operations != null);
        this.operations = operations;
    }

    @Override
    public SearchPage search(String query, Pageable pageable, FilterParameters filterParameters, ProfileParameters profileParameters, Collection<Status> statusCollection) {

        Page<Course> coursePage = executeSearchQuery(query, pageable, filterParameters, profileParameters, statusCollection);

        SearchPage searchPage = new SearchPage();
        searchPage.setCourses(coursePage);

        return searchPage;
    }

    private Page<Course> executeSearchQuery(String query, Pageable pageable, FilterParameters filterParameters, ProfileParameters profileParameters, Collection<Status> statusCollection) {

        BoolQueryBuilder boolQuery = boolQuery();

        if (isNotBlank(query)) {
            boolQuery = boolQuery.must(QueryBuilders.multiMatchQuery(query)
                    .field("title", 8)
                    .field("shortDescription", 4)
                    .field("description", 2)
                    .field("learningOutcomes", 2)
                    .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                    .fuzziness(Fuzziness.ONE));
        }

        if (filterParameters.hasTypes()) {
            BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();

            for (String type : filterParameters.getTypes()) {
                filterQuery = filterQuery
                        .should(QueryBuilders.matchQuery("modules.type", type))
                        .should(QueryBuilders.matchQuery("type", type));
            }
            filterQuery.minimumShouldMatch(1);
            boolQuery = boolQuery.must(filterQuery);
        }

        boolQuery = addFilter(boolQuery, filterParameters.getAreasOfWork(), "audiences.areasOfWork");
        boolQuery = addFilter(boolQuery, filterParameters.getDepartments(), "audiences.departments");
        boolQuery = addFilter(boolQuery, filterParameters.getInterests(), "audiences.interests");

        if (filterParameters.hasCost()) {
            boolQuery = boolQuery
                    .must(QueryBuilders.boolQuery()
                            .should(QueryBuilders.matchQuery("modules.cost", 0))
                            .minimumShouldMatch(1));
        }

        List<String> statusList = new ArrayList<>();
        statusCollection.forEach(s -> statusList.add(s.getValue()));

        boolQuery = addFilter(boolQuery, statusList, "status");

        BoolQueryBuilder filterQuery = boolQuery();
        filterQuery.should(QueryBuilders.matchQuery("visibility", "PUBLIC"));

        addOrFilter(filterQuery, profileParameters.getProfileDepartments(), "audiences.departments");
        addOrFilter(filterQuery, profileParameters.getProfileGrades(), "audiences.grades");
        addOrFilter(filterQuery, profileParameters.getProfileAreasOfWork(), "audiences.areasOfWork");
        addOrFilter(filterQuery, profileParameters.getProfileInterests(), "audiences.interests");

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withFilter(filterQuery)
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

    private BoolQueryBuilder addOrFilter(BoolQueryBuilder boolQuery, List<String> values, String key) {
        if (values != null && !values.isEmpty()) {
            for(String value : values) {
                boolQuery.should(QueryBuilders.matchQuery(key, value));
            }
        }

        return boolQuery;
    }
}
