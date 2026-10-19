package uk.gov.cslearning.catalogue.repository.elastic;

import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.ScriptSortType;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.catalogue.Utils;
import uk.gov.cslearning.catalogue.api.OwnerParameters;
import uk.gov.cslearning.catalogue.api.SearchResults;
import uk.gov.cslearning.catalogue.api.v2.model.CourseSearchParameters;
import uk.gov.cslearning.catalogue.domain.Course;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Repository
public class CourseSearchRepositoryImpl implements CourseSearchRepository {

    private ElasticsearchOperations operations;

    public CourseSearchRepositoryImpl(ElasticsearchOperations operations) {
        checkArgument(operations != null);
        this.operations = operations;
    }

    @Override
    public SearchResults search(Pageable pageable, CourseSearchParameters courseSearchParameters, String sortField, Sort.Direction sortDirection) {
        Query searchQuery = getSearchQuery(pageable, courseSearchParameters, sortField, sortDirection).getQuery();
        Page<Course> coursePage = Utils.searchPageToPage(operations.search((org.springframework.data.elasticsearch.core.query.Query) searchQuery, Course.class), pageable);
        return new SearchResults(coursePage, pageable);
    }

    @Override
    public SearchResults search(Pageable pageable, CourseSearchParameters courseSearchParameters) {
        NativeQuery searchQuery = getSearchQuery(pageable, courseSearchParameters);
        Page<Course> coursePage = Utils.searchPageToPage(operations.search(searchQuery, Course.class), pageable);
        return new SearchResults(coursePage, pageable);
    }

    @Override
    public SearchResults search(Pageable pageable, CourseSearchParameters courseSearchParameters, OwnerParameters ownerParameters) {
        Query filterQuery = getOwnerFilter(ownerParameters);

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(getSearchBuilderQuery(courseSearchParameters))
                .withFilter(filterQuery)
                .withPageable(pageable)
                .build();

        Page<Course> coursePage = Utils.searchPageToPage(operations.search(searchQuery, Course.class), pageable);
        return new SearchResults(coursePage, pageable);
    }

    private Query getSearchBuilderQuery(CourseSearchParameters parameters) {
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        String queryStr = parameters.getQuery();
        if (isNotBlank(queryStr)) {
            boolQueryBuilder.must(QueryBuilders.multiMatch(m -> m
                    .query(queryStr)
                    // Weights are now appended directly to the field name using the caret (^) symbol
                    .fields("title^8", "shortDescription^4", "description^2", "learningOutcomes^2")
                    .type(TextQueryType.BestFields)
                    .fuzziness("1")
                    .operator(Operator.And)
            ));
        }

        if (parameters.getTitleStartsWith() != null) {
            boolQueryBuilder.must(QueryBuilders.prefix(p -> p
                    .field("title.keyword")
                    .value(parameters.getTitleStartsWith().trim())
                    .caseInsensitive(true)
            ));
        }

        if (parameters.costIsFree()) {
            boolQueryBuilder.must(QueryBuilders.match(m -> m
                    .field("cost")
                    .query("0")
                    .operator(Operator.And)
            ));
        }

        return Query.of(q -> q.bool(boolQueryBuilder.build()));
    }

    private Query getModuleTypeBoolQuery(List<String> moduleTypes) {
        BoolQuery.Builder typesQuery = new BoolQuery.Builder();

        for (String type : moduleTypes) {
            typesQuery
                    .should(QueryBuilders.match(m -> m.field("modules.type").query(type)))
                    .should(QueryBuilders.match(m -> m.field("type").query(type)));
        }

        typesQuery.minimumShouldMatch("1");
        return Query.of(q -> q.bool(typesQuery.build()));
    }

    private Query getAudienceNestedQuery(List<String> departments, List<String> areasOfWork, List<String> interests) {
        BoolQuery.Builder audiencesBoolQuery = new BoolQuery.Builder();

        departments.forEach(department -> audiencesBoolQuery.must(
                QueryBuilders.match(m -> m.field("audiences.departments").query(department).operator(Operator.And))));

        areasOfWork.forEach(areaOfWork -> audiencesBoolQuery.must(
                QueryBuilders.match(m -> m.field("audiences.areasOfWork").query(areaOfWork).operator(Operator.And))));

        interests.forEach(interest -> audiencesBoolQuery.must(
                QueryBuilders.match(m -> m.field("audiences.interests").query(interest).operator(Operator.And))));

        return QueryBuilders.nested(n -> n
                .path("audiences")
                .query(q -> q.bool(audiencesBoolQuery.build()))
                .scoreMode(ChildScoreMode.Avg)
        );
    }

    private Query getOwnerFilter(OwnerParameters ownerParameters) {
        BoolQuery.Builder filterQuery = new BoolQuery.Builder();

        if (ownerParameters.hasOrganisationalUnitCode()) {
            filterQuery.must(QueryBuilders.match(m -> m
                    .field("owner.organisationalUnit")
                    .query(ownerParameters.getOrganisationalUnitCode())));
        }

        if (ownerParameters.hasProfession()) {
            filterQuery.must(QueryBuilders.match(m -> m
                    .field("owner.profession")
                    .query(ownerParameters.getProfession().toString())));
        }

        if (ownerParameters.hasSupplier()) {
            filterQuery.must(QueryBuilders.match(m -> m
                    .field("owner.supplier")
                    .query(ownerParameters.getSupplier())));
        }

        return Query.of(q -> q.bool(filterQuery.build()));
    }

    private NativeQuery getSearchQuery(Pageable pageable, CourseSearchParameters courseSearchParameters, String fieldName, Sort.Direction direction) {
        SortOrder esSortOrder = direction.isAscending() ? SortOrder.Asc : SortOrder.Desc;
        String scriptSource = String.format("doc['%s.keyword'].value.toLowerCase()", fieldName);

        Script inlineScript = Script.of(s -> s.source(scriptSource));

        SortOptions sortOptions = SortOptions.of(s -> s
                .script(ss -> ss
                        .script(inlineScript)
                        .type(ScriptSortType.String)
                        .order(esSortOrder)
                )
        );

        return NativeQuery.builder()
                .withQuery(getSearchBuilderQuery(courseSearchParameters))
                .withSort(sortOptions)
                .withPageable(pageable)
                .build();
    }

    private NativeQuery getSearchQuery(CourseSearchParameters courseSearchParameters) {
        Query mainQuery = getSearchBuilderQuery(courseSearchParameters);

        BoolQuery.Builder filterQueryBuilder = new BoolQuery.Builder();
        courseSearchParameters.getVisibility().forEach(v ->
                filterQueryBuilder.should(QueryBuilders.match(m -> m
                        .field("visibility")
                        .query(v.toString())
                ))
        );

        return NativeQuery.builder()
                .withQuery(mainQuery)
                .withFilter(Query.of(q -> q.bool(filterQueryBuilder.build())))
                .build();
    }

    private NativeQuery getSearchQuery(Pageable pageable, CourseSearchParameters courseSearchParameters) {
        SortOptions scoreSort = SortOptions.of(s -> s.score(score -> score));

        return NativeQuery.builder()
                .withQuery(getSearchBuilderQuery(courseSearchParameters))
                .withSort(scoreSort)
                .withPageable(pageable)
                .build();
    }

    @Override
    public Page<Course> findAllByOrganisationCode(String organisationalUnitCode, Pageable pageable) {
        Query boolQuery = Query.of(q -> q.bool(b -> b
                .must(QueryBuilders.match(m -> m
                        .field("owner.organisationalUnit")
                        .query(organisationalUnitCode)))
        ));

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withPageable(pageable)
                .build();

        return Utils.searchPageToPage(operations.search(searchQuery, Course.class), pageable);
    }

    @Override
    public Page<Course> findAllBySupplier(String supplier, Pageable pageable) {
        Query boolQuery = Query.of(q -> q.bool(b -> b
                .must(QueryBuilders.match(m -> m
                        .field("owner.supplier")
                        .query(supplier)))
        ));

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withPageable(pageable)
                .build();

        return Utils.searchPageToPage(operations.search(searchQuery, Course.class), pageable);
    }

    private void addFilter(BoolQuery.Builder boolQuery, List<String> values, String key) {
        if (values != null && !values.isEmpty()) {
            BoolQuery.Builder filterQuery = new BoolQuery.Builder();

            for (String department : values) {
                filterQuery.should(QueryBuilders.matchPhrase(m -> m
                        .field(key)
                        .query(department)));
            }
            filterQuery.minimumShouldMatch("1");
            boolQuery.must(Query.of(q -> q.bool(filterQuery.build())));
        }
    }

}
