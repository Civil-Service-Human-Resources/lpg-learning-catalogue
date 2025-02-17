package uk.gov.cslearning.catalogue.integration;


import org.junit.Before;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.cslearning.catalogue.util.DataService;
import uk.gov.cslearning.catalogue.config.DisableBlobStorage;
import uk.gov.cslearning.catalogue.config.ElasticContainer;
import uk.gov.cslearning.catalogue.config.IntegrationTestConfig;

import javax.annotation.PostConstruct;

@ActiveProfiles("integration-test")
@RunWith(SpringRunner.class)
@Import({IntegrationTestConfig.class, ElasticContainer.class, DisableBlobStorage.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.main.allow-bean-definition-overriding=true"})
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class IntegrationTestBase {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected DataService dataService;

    protected boolean coursesLoaded = false;

    @Before
    public void beforeAll() {
        dataService.loadBulkCourses();
    }

    @PostConstruct
    public void afterAll() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> dataService.deleteBulkCourses()));
    }

    protected ResultActions submitRequest(RequestBuilder requestBuilder) throws Exception {
        return mvc.perform(requestBuilder)
                .andDo(result -> {
                    System.out.println("API RESPONSE");
                    String json = result.getResponse().getContentAsString();
                    System.out.println(json);
                });
    }

}
