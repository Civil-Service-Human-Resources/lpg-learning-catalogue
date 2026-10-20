package uk.gov.cslearning.catalogue.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import org.apache.tika.Tika;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.cslearning.catalogue.config.DisableBlobStorage;
import uk.gov.cslearning.catalogue.config.IntegrationTestConfig;
import uk.gov.cslearning.catalogue.config.UploadConfig;
import uk.gov.cslearning.catalogue.repository.elastic.CourseRepository;
import uk.gov.cslearning.catalogue.repository.elastic.MediaRepository;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;
import uk.gov.cslearning.catalogue.util.DataService;

@ActiveProfiles("mysql")
@RunWith(SpringRunner.class)
@Import({IntegrationTestConfig.class, DisableBlobStorage.class})
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.main.allow-bean-definition-overriding=true",
                // Exclude the H2 console completely to prevent Spring Security MVC servlet ambiguity
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration"
        }
)
@AutoConfigureMockMvc
@WithMockUser(username = "user")
public abstract class MySQLIntegrationTestBase {

    /*
    Mock out the Elasticsearch repositories to prevent the app connecting to ES.
    The ES tests should be moved to their own package at some point, maybe after the Java upgrade / maven migration
     */

    @MockitoBean
    private CourseRepository courseRepository;

    @MockitoBean
    private MediaRepository mediaRepository;

    // Add this mock to satisfy UploadConfig dependency
    @MockitoBean
    private CloudStorageAccount cloudStorageAccount;

    // Use deep stubs so getContainerReference() returns a mock instead of null,
    // seamlessly bypassing the NPE in UploadConfig
    @MockitoBean(answers = Answers.RETURNS_DEEP_STUBS)
    private CloudBlobClient cloudBlobClient;

    @MockitoBean
    private UploadConfig uploadConfig;

    @MockitoBean
    private Tika tika;

    // Add this mock to satisfy the MediaController/FileUploadService dependency
    @MockitoBean(name = "existing_container")
    private UploadClient existingUploadClient;

    // Add this mock as well, as it is likely the companion bean to existing_container
    @MockitoBean(name = "new_container")
    private UploadClient newUploadClient;

    // Add this mock to satisfy the learning_material dependency required by ScormFileUploadService
    @MockitoBean(name = "learning_material")
    private UploadClient learningMaterialUploadClient;

    // Add this mock to satisfy the OAuth2 Resource Server configuration
    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    protected DataService dataService;

    @Autowired
    protected WebApplicationContext context;
}
