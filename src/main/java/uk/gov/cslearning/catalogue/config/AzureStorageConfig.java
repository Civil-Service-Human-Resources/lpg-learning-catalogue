package uk.gov.cslearning.catalogue.config;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureStorageConfig {
    @Autowired
    private CloudStorageAccount cloudStorageAccount;

    @Bean
    public CloudBlobClient storageClient() {
        return cloudStorageAccount.createCloudBlobClient();
    }


}
