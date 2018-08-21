package uk.gov.cslearning.catalogue;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

/**
 * Main Spring application configuration and entry point.
 */
@SpringBootApplication(exclude = {ElasticsearchAutoConfiguration.class, ElasticsearchDataAutoConfiguration.class})
public class Application {

    @Autowired
    private CloudStorageAccount cloudStorageAccount;

    @Bean
    public CloudBlobClient storageClient() throws URISyntaxException, InvalidKeyException {
        return cloudStorageAccount.createCloudBlobClient();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
