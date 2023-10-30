package uk.gov.cslearning.catalogue.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.glassfish.jersey.servlet.WebConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;
import uk.gov.cslearning.catalogue.api.v2.CourseControllerV2;
import uk.gov.cslearning.catalogue.api.v2.model.CourseSearchParameters;
import uk.gov.cslearning.catalogue.domain.Course;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(CourseControllerV2.class)
@WithMockUser(username = "user")
@ContextConfiguration(classes = {WebConfig.class, CourseControllerV2.class})
@EnableSpringDataWebSupport
@TestPropertySource(properties = {"elasticsearch.host = localhost", "elasticsearch.protocol = http", "elasticsearch.port = 9200"})
public class CourseControllerTCTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    Environment environment;

    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    private CourseSearchRepository courseSearchRepository;

    private ObjectMapper objectMapper;

    ElasticsearchContainer container = createElasticSearchContainer();

    @Before
    public void setUp(){
        objectMapper = new ObjectMapper();
    }

    String courseWithDepartment = "{ \"_class\": \"uk.gov.cslearning.catalogue.domain.Course\", \"id\": \"7CYp2UzNQsKZFoiEmBFeIA\", \"title\": \"Googling part I\", \"shortDescription\": \"dkflgj\", \"description\": \"sdkfjg\", \"learningOutcomes\": \"\", \"modules\": [], \"learningProvider\": { \"id\": \"Yuu9Y5kPQtO9tnZCOR-IqA\", \"termsAndConditions\": [], \"cancellationPolicies\": [], \"status\": \"Published\" }, \"audiences\": [ { \"id\": \"S3y1WC1fQluuhx_RKybQ2w\", \"name\": \"\", \"areasOfWork\": [], \"departments\": [ \"co\" ], \"grades\": [], \"interests\": [], \"type\": \"OPEN\" } ], \"preparation\": \"\", \"owner\": { \"scope\": \"GLOBAL\", \"organisationalUnit\": \"co\", \"profession\": 1, \"supplier\": \"\" }, \"visibility\": \"PUBLIC\", \"status\": \"Published\", \"topicId\": \"\", \"createdTimestamp\": \"2023-10-26T09:18:42\", \"updatedTimestamp\": \"2023-10-26T09:19:39\", \"cost\": 0}";

    String courseWithoutDepartment = "{\n" +
            "  \"_class\": \"uk.gov.cslearning.catalogue.domain.Course\",\n" +
            "  \"id\": \"xYzExF86Qo6n5BT1h6ci4w\",\n" +
            "  \"title\": \"Googling for all\",\n" +
            "  \"shortDescription\": \"sdf\",\n" +
            "  \"description\": \"dsfg\",\n" +
            "  \"learningOutcomes\": \"\",\n" +
            "  \"modules\": [],\n" +
            "  \"learningProvider\": {\n" +
            "    \"id\": \"89mfT_riTTqw4ErthHDzyA\",\n" +
            "    \"termsAndConditions\": [],\n" +
            "    \"cancellationPolicies\": [],\n" +
            "    \"status\": \"Published\"\n" +
            "  },\n" +
            "  \"audiences\": [],\n" +
            "  \"preparation\": \"\",\n" +
            "  \"owner\": {\n" +
            "    \"scope\": \"GLOBAL\",\n" +
            "    \"organisationalUnit\": \"co\",\n" +
            "    \"profession\": 1,\n" +
            "    \"supplier\": \"\"\n" +
            "  },\n" +
            "  \"visibility\": \"PUBLIC\",\n" +
            "  \"status\": \"Published\",\n" +
            "  \"topicId\": \"\",\n" +
            "  \"createdTimestamp\": \"2023-10-26T09:20:05\",\n" +
            "  \"updatedTimestamp\": \"2023-10-26T09:20:08\",\n" +
            "  \"cost\": 0\n" +
            "}";

    @Test

    public void test1() throws Exception {

        populateElasticSearchContainer();

        CourseSearchParameters parameters = new CourseSearchParameters();
        parameters.setSearchTerm("googling");

        mockMvc.perform(get("/v2/courses/search")
               .content(objectMapper.writeValueAsString(parameters))
                .with(csrf()));
               .contentType(MediaType.APPLICATION_JSON)
               .accept(MediaType.APPLICATION_JSON));


       SearchResponse searchResponse = performSearch();

       assertTrue(searchResponse.getHits().getTotalHits().value > 0);
       assertEquals(2, searchResponse.getHits().getHits().length);
    }



    private ElasticsearchContainer createElasticSearchContainer(){
        String esDockerImage = "docker.elastic.co/elasticsearch/elasticsearch:8.4.2";

        ElasticsearchContainer container = new ElasticsearchContainer(DockerImageName.parse(esDockerImage));
        container.addExposedPort(9200);
        container.addExposedPort(9300);
        container.withEnv("discovery.type", "single-node");
        container.withEnv("cluster.name", "local");
        container.withEnv("xpack.security.enabled", "false");
        container.start();

        return container;
    }

    private void populateElasticSearchContainer() throws IOException {
        RestClient restClient = RestClient.builder(HttpHost.create(container.getHttpHostAddress())).build();

        Request r = new Request("POST", "/courses/_doc");
        r.setJsonEntity(courseWithDepartment);
        restClient.performRequest(r);

        Request r2 = new Request("POST", "/courses/_doc");
        r2.setJsonEntity(courseWithoutDepartment);
        restClient.performRequest(r2);

        restClient.close();
    }

    private SearchResponse performSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("courses");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("title", "googling"));
        searchRequest.source(searchSourceBuilder);

        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(RestClient.builder(HttpHost.create(container.getHttpHostAddress())));
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        return searchResponse;
    }
}
