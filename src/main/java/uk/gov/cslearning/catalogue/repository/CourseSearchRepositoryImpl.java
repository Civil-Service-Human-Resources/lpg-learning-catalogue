package uk.gov.cslearning.catalogue.repository;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.catalogue.Utils;
import uk.gov.cslearning.catalogue.api.FilterParameters;
import uk.gov.cslearning.catalogue.api.OwnerParameters;
import uk.gov.cslearning.catalogue.api.ProfileParameters;
import uk.gov.cslearning.catalogue.api.v2.model.CourseSearchParameters;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.SearchPage;
import uk.gov.cslearning.catalogue.domain.Status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.elasticsearch.index.query.QueryBuilders.*;

@Repository
public class CourseSearchRepositoryImpl implements CourseSearchRepository {

    private ElasticsearchOperations operations;

    public CourseSearchRepositoryImpl(ElasticsearchOperations operations) {
        checkArgument(operations != null);
        this.operations = operations;
    }

    @Override
    public SearchPage search(String query, Pageable pageable, FilterParameters filterParameters, Collection<Status> statusCollection, OwnerParameters ownerParameters, ProfileParameters profileParameters, String visbility) {

        Page<Course> coursePage = executeSearchQuery(query, pageable, filterParameters, statusCollection, ownerParameters, profileParameters, visbility);

        SearchPage searchPage = new SearchPage();
        searchPage.setCourses(coursePage);

        return searchPage;
    }

    public Page<Course> search(CourseSearchParameters parameters, Pageable pageable) {
        BoolQueryBuilder searchQuery = getSearchQuery(parameters);

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(searchQuery)
                .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .withPageable(pageable)
                .build();

        return Utils.searchPageToPage(operations.search(query, Course.class), pageable);

    }

    private BoolQueryBuilder getSearchQuery(CourseSearchParameters parameters){
        BoolQueryBuilder searchQuery = boolQuery();

        if(!parameters.getSearchTerm().isEmpty()) {
            searchQuery.must(multiMatchQuery(parameters.getSearchTerm())
                    .field("title", 8)
                    .field("shortDescription", 4)
                    .field("description", 2)
                    .field("learningOutcomes", 2)
                    .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                    .fuzziness(Fuzziness.ONE)
                    .operator(Operator.AND));
        }

        searchQuery.must(matchQuery("status", "Published").operator(Operator.AND));

        if(!parameters.getTypes().isEmpty()) {
            BoolQueryBuilder typesQuery = boolQuery();
            parameters.getTypes().forEach(type -> typesQuery.should(matchQuery("modules.type", type)));
            parameters.getTypes().forEach(type -> typesQuery.should(matchQuery("type", type)));

            typesQuery.minimumShouldMatch(1);
            searchQuery.must(typesQuery);
        }

        if(parameters.costIsFree()) searchQuery.must(matchQuery("cost", 0).operator(Operator.AND));

        if(parameters.hasAudienceFields()) {
            searchQuery.must(getAudienceNestedQuery(
                    parameters.getDepartments(),
                    parameters.getAreasOfWork(),
                    parameters.getInterests()));
        }


        return searchQuery;
    }

    private NestedQueryBuilder getAudienceNestedQuery(List<String> departments, List<String> areasOfWork, List<String> interests){
        BoolQueryBuilder audiencesBoolQuery = boolQuery();

        departments.forEach(department -> audiencesBoolQuery.must(matchQuery("audiences.departments", department).operator(Operator.AND)));
        areasOfWork.forEach(areaOfWork -> audiencesBoolQuery.must(matchQuery("audiences.areasOfWork", areaOfWork).operator(Operator.AND)));
        interests.forEach(interest -> audiencesBoolQuery.must(matchQuery("audiences.interests", interest).operator(Operator.AND)));

        NestedQueryBuilder audiencesQuery = nestedQuery("audiences", audiencesBoolQuery, ScoreMode.Avg);
        return audiencesQuery;
    }

    private Page<Course> executeSearchQuery(String query, Pageable pageable, FilterParameters filterParameters, Collection<Status> statusCollection, OwnerParameters ownerParameters, ProfileParameters profileParameters, String visibility) {
        BoolQueryBuilder boolQuery = boolQuery();

        if (isNotBlank(query)) {
            boolQuery = boolQuery.must(multiMatchQuery(query)
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
                        .should(matchQuery("modules.type", type))
                        .should(matchQuery("type", type));
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
                            .should(matchQuery("cost", 0)));
        }

        List<String> statusList = new ArrayList<>();
        statusCollection.forEach(s -> statusList.add(s.getValue()));

        boolQuery = addFilter(boolQuery, statusList, "status");

        BoolQueryBuilder filterQuery = boolQuery();

        if (ownerParameters.hasOrganisationalUnitCode()) {
            filterQuery.must(matchQuery("owner.organisationalUnit", ownerParameters.getOrganisationalUnitCode()));
        }

        if (ownerParameters.hasProfession()) {
            filterQuery.must(matchQuery("owner.profession", ownerParameters.getProfession()));
        }

        if (ownerParameters.hasSupplier()) {
            filterQuery.must(matchQuery("owner.supplier", ownerParameters.getSupplier()));
        }

        if (visibility.equals("PUBLIC")) {
            filterQuery.should(matchQuery("visibility", "PUBLIC"));
        }

        addOrFilter(filterQuery, profileParameters.getProfileDepartments(), "audiences.departments");
        addOrFilter(filterQuery, profileParameters.getProfileGrades(), "audiences.grades");
        addOrFilter(filterQuery, profileParameters.getProfileAreasOfWork(), "audiences.areasOfWork");
        addOrFilter(filterQuery, profileParameters.getProfileInterests(), "audiences.interests");

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withFilter(filterQuery)
                .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .withPageable(pageable)
                .build();

        return Utils.searchPageToPage(operations.search(searchQuery, Course.class), pageable);
    }

    @Override
    public Page<Course> findAllByOrganisationCode(String organisationalUnitCode, Pageable pageable) {
        BoolQueryBuilder boolQuery = boolQuery();

        boolQuery.must(matchQuery("owner.organisationalUnit", organisationalUnitCode));

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(pageable)
                .build();

        return Utils.searchPageToPage(operations.search(searchQuery, Course.class), pageable);
    }

    @Override
    public Page<Course> findAllByProfessionId(String professionId, Pageable pageable) {
        BoolQueryBuilder boolQuery = boolQuery();

        boolQuery.must(matchQuery("owner.profession", professionId));

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(pageable)
                .build();

        return Utils.searchPageToPage(operations.search(searchQuery, Course.class), pageable);
    }

    @Override
    public Page<Course> findAllBySupplier(String supplier, Pageable pageable) {
        BoolQueryBuilder boolQuery = boolQuery();

        boolQuery.must(matchQuery("owner.supplier", supplier));

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(pageable)
                .build();

        return Utils.searchPageToPage(operations.search(searchQuery, Course.class), pageable);
    }


    private BoolQueryBuilder addFilter(BoolQueryBuilder boolQuery, List<String> values, String key) {
        if (values != null && !values.isEmpty()) {
            BoolQueryBuilder filterQuery = QueryBuilders.boolQuery();

            for (String department : values) {
                filterQuery = filterQuery
                        .should(QueryBuilders.matchPhraseQuery(key, department));
            }
            filterQuery.minimumShouldMatch(1);
            return boolQuery.must(filterQuery);
        }
        return boolQuery;
    }

    private BoolQueryBuilder addOrFilter(BoolQueryBuilder boolQuery, List<String> values, String key) {
        if (values != null && !values.isEmpty()) {
            for (String value : values) {
                boolQuery.should(QueryBuilders.matchPhraseQuery(key, value));
            }
        }

        return boolQuery;
    }
}
