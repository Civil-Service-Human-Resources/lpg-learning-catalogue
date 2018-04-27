package uk.gov.cslearning.catalogue.repository;

import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.catalogue.api.FilterParameters;
import uk.gov.cslearning.catalogue.domain.Resource;
import uk.gov.cslearning.catalogue.domain.SearchPage;

import static com.google.common.base.Preconditions.checkArgument;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;


@Repository
public class ResourceSearchRepositoryImpl implements ResourceSearchRepository {

    private static Logger LOGGER = LoggerFactory.getLogger(ResourceSearchRepositoryImpl.class);

    private ElasticsearchOperations operations;

    public ResourceSearchRepositoryImpl(ElasticsearchOperations operations) {
        checkArgument(operations != null);
        this.operations = operations;
    }

    @Override
    public SearchPage search(String query, Pageable pageable,FilterParameters filterParameters) {
        SearchPage searchPage = new SearchPage();

        Page<Resource> resourcePage = executeSearchQuery(query, pageable,filterParameters);

        searchPage.setResources(resourcePage);

        return searchPage;
    }


    public Page<Resource> executeSearchQuery(String query, Pageable pageable, FilterParameters filterParameters) {

        BoolQueryBuilder boolQuery =  boolQuery().must(QueryBuilders.multiMatchQuery(query)
                .field("title", 8)
                .field("shortDescription", 4)
                .field("description", 2)
                .field("learningOutcomes", 2)
                .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                .fuzziness(Fuzziness.ONE)
                );

        if (filterParameters.getType() != null && !filterParameters.getType().equals("")) {
            // check if we have a number of types
            String[] types = filterParameters.getType().split(",");
            BoolQueryBuilder  filterQuery =QueryBuilders.boolQuery();

            for ( String type : types) { // should = OR but not restrictive
                filterQuery = filterQuery.should(QueryBuilders.matchQuery("modules.type", type));
                // for modules
                filterQuery = filterQuery.should(QueryBuilders.matchQuery("type", type));
            }
            filterQuery.minimumShouldMatch(1); // implies restriction
            boolQuery= boolQuery.must(filterQuery);
        }

       if (filterParameters.getCost() != null && !filterParameters.getCost().equals("")) {
            // only one possible value right now
                boolQuery= boolQuery
                        .must(
                                QueryBuilders.boolQuery()
                                        .should(QueryBuilders.matchQuery("modules.price", 0))
                                        .should(QueryBuilders.matchQuery("price", 0))
                                        .minimumShouldMatch(1)
                        );
        }


        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(
                    boolQuery
                )
                .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("courseId").order(SortOrder.ASC)) // always want modules to come after courses
                .withPageable(pageable)
                .build();

        return operations.queryForPage(searchQuery, Resource.class);

    }
}
 
