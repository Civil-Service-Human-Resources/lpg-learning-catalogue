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
import uk.gov.cslearning.catalogue.domain.Resource;
import uk.gov.cslearning.catalogue.domain.SearchPage;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;


@Repository
public class ResourceSearchRepositoryImpl implements ResourceSearchRepository {

    private ElasticsearchOperations operations;

    public ResourceSearchRepositoryImpl(ElasticsearchOperations operations) {
        checkArgument(operations != null);
        this.operations = operations;
    }

    @Override
    public SearchPage search(String query, Pageable pageable, FilterParameters filterParameters) {

        Page<Resource> resourcePage = executeSearchQuery(query, pageable, filterParameters);

        SearchPage searchPage = new SearchPage();
        searchPage.setResources(resourcePage);

        return searchPage;
    }

    private Page<Resource> executeSearchQuery(String query, Pageable pageable, FilterParameters filterParameters) {

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

        boolQuery = addFilter(boolQuery, filterParameters.getAreasOfWork(), "modules.audiences.areasOfWork");
        boolQuery = addFilter(boolQuery, filterParameters.getDepartments(), "modules.audiences.departments");
        boolQuery = addFilter(boolQuery, filterParameters.getInterests(), "modules.audiences.interests");

        if (filterParameters.hasCost()) {
            boolQuery = boolQuery
                    .must(QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchQuery("modules.price", 0))
                        .should(QueryBuilders.matchQuery("price", 0))
                        .minimumShouldMatch(1));
        }

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("courseId").order(SortOrder.ASC)) // always want modules to come after courses
                .withPageable(pageable)
                .build();

        return operations.queryForPage(searchQuery, Resource.class);
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
}

