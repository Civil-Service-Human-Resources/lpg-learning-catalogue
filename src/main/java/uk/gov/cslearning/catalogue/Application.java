package uk.gov.cslearning.catalogue;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import org.springframework.beans.factory.annotation.Value;
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

    @Bean
    public CloudBlobClient storageClient(
        @Value("${azure.account.name}") final String azureAccountName,
        @Value("${azure.account.key}") final String azureAccountKey
    ) throws URISyntaxException, InvalidKeyException {
        final String connectionString = String.format("DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s",
                azureAccountName, azureAccountKey);

        final CloudStorageAccount account = CloudStorageAccount.parse(connectionString);
        return account.createCloudBlobClient();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
