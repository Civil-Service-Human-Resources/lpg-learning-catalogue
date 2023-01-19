package uk.gov.cslearning.catalogue.config;

import com.google.common.collect.ImmutableMap;
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
import uk.gov.cslearning.catalogue.service.upload.*;
import uk.gov.cslearning.catalogue.service.upload.client.AzureUploadClient;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;

import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URISyntaxException;
import java.util.Map;

@Configuration
@Slf4j
public class UploadConfig {

    @Bean(name = "learning_material")
    public UploadClient scormAzureUploadClient(CloudBlobClient client, @Value("${azure.storage.rustici-container}") String containerName) {
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

    @Bean("fileUploadServiceMap")
    public Map<String, FileUploadService> fileProcessorMap(
            ScormFileUploadService scormFileUploadService,
            ImageFileUploadService imageFileUploadService,
            Mp4FileUploadService mp4FileUploadService,
            DefaultFileUploadService defaultFileUploadService
    ) {
        return ImmutableMap.<String, FileUploadService>builder()
                .put("doc",  defaultFileUploadService) // MS Word
                .put("docx", defaultFileUploadService) // MS Word
                .put("pdf",  defaultFileUploadService) // PDF
                .put("ppsm", defaultFileUploadService) // MS PowerPoint
                .put("ppt",  defaultFileUploadService) // MS PowerPoint
                .put("pptx", defaultFileUploadService) // MS PowerPoint
                .put("xls",  defaultFileUploadService) // MS Excel
                .put("xlsx", defaultFileUploadService) // MS Excel
                .put("zip", scormFileUploadService) // Scorm
                .put("mp4",  mp4FileUploadService)     // Video
                .put("jpg", imageFileUploadService) // img
                .put("jpeg", imageFileUploadService) // img
                .put("png", imageFileUploadService) // img
                .put("svg", imageFileUploadService) // img
                .build();
    }

    @Bean
    public Tika tika() {
        return new Tika();
    }

    @Bean
    public DocumentBuilderFactory documentBuilderFactory() {
        return DocumentBuilderFactory.newInstance();
    }

}
