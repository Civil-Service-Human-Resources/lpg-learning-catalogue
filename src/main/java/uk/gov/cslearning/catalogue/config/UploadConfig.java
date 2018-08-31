package uk.gov.cslearning.catalogue.config;

import com.google.common.collect.ImmutableMap;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.cslearning.catalogue.service.upload.uploader.DefaultUploader;
import uk.gov.cslearning.catalogue.service.upload.uploader.ScormUploader;
import uk.gov.cslearning.catalogue.service.upload.uploader.Uploader;

import java.util.Map;
import java.util.function.Supplier;

@Configuration
public class UploadConfig {
    @Autowired
    private CloudStorageAccount cloudStorageAccount;

    @Bean
    public CloudBlobClient storageClient() {
        return cloudStorageAccount.createCloudBlobClient();
    }

    @Bean(name = "uploaderFactoryMethods")
    public Map<String, Supplier<Uploader>> uploaderFactoryMethods(
            DefaultUploader defaultUploader,
            ScormUploader scormUploader
    ) {
        return ImmutableMap.of(
                "doc", () -> defaultUploader,
                "zip", () -> scormUploader
        );
    }
}
