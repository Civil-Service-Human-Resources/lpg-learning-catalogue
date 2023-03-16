package uk.gov.cslearning.catalogue.config;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.cslearning.catalogue.service.upload.client.AzureUploadClient;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

@Configuration
@Slf4j
public class UploadConfig {

    @Bean(name = "learning_material")
    public UploadClient scormAzureUploadClient(CloudBlobClient client, @Value("${azure.storage.scorm-container}") String containerName) {
        CloudBlobContainer container = createBlobContainer(client, containerName);
        return new AzureUploadClient(container);
    }

    @Bean(name = "existing_container")
    public UploadClient existingAzureUploadClient(CloudBlobClient client, @Value("${azure.storage.container}") String containerName) {
        CloudBlobContainer container = createBlobContainer(client, containerName);
        return new AzureUploadClient(container);
    }

    @SneakyThrows
    private CloudBlobContainer createBlobContainer(CloudBlobClient client, String containerName) {
        try {
            CloudBlobContainer container = client.getContainerReference(containerName);
            container.createIfNotExists();
            return container;
        } catch (StorageException | URISyntaxException e) {
            log.error(String.format("Failed to get blob container \"%s\"", containerName), e);
            throw e;
        }
    }

    @Bean
    public CloudBlobClient storageClient(CloudStorageAccount cloudStorageAccount) {
        return cloudStorageAccount.createCloudBlobClient();
    }

    @Bean
    public Tika tika() {
        return new Tika();
    }

    @Bean(name = "elearning_manifest_list")
    public List<String> getElearningManifests(@Value("rustici.e-learning-manifests") String eLearningManifestsCsv) {
        return Arrays.asList(eLearningManifestsCsv.split(","));
    }

}
