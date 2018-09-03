package uk.gov.cslearning.catalogue.config;

import com.google.common.collect.ImmutableMap;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.cslearning.catalogue.domain.media.MediaEntity;
import uk.gov.cslearning.catalogue.domain.media.factory.CreateDocumentFunction;
import uk.gov.cslearning.catalogue.domain.media.factory.CreateScormFunction;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.service.upload.uploader.DefaultUploader;
import uk.gov.cslearning.catalogue.service.upload.uploader.ScormUploader;
import uk.gov.cslearning.catalogue.service.upload.uploader.Uploader;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
public class UploadConfig {
    @Bean
    public CloudBlobClient storageClient(CloudStorageAccount cloudStorageAccount) {
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

    @Bean(name="mediaEntityFactoryMethods")
    public Map<String, Function<Upload, MediaEntity>> mediaEntityFactoryMethods(
            CreateDocumentFunction createDocumentFunction,
            CreateScormFunction createScormFunction
    ){
        return ImmutableMap.of(
                "doc", createDocumentFunction,
                "zip", createScormFunction
        );
    }

    @Bean("fileSubstitions")
    public Map<String, String> fileSubstitions() {
        return ImmutableMap.of(
                /* file-to-substitute => substituted-with */
                "js/player_management/close_methods.js", "/file-substitutions/close_methods.js", //GOMO
                "js/player_management/content_tracking/adapters/tincan_wrapper.js", "/file-substitutions/tincan_wrapper.js", //GOMO
                "js/player_management/portal_overrides.js", "/file-substitutions/portal_overrides.js", //GOMO
                "story_content/user.js", "/file-substitutions/user.js", //Storyline
                "SCORMDriver/Configuration.js", "/file-substitutions/Configuration.js" // DominKNOW
        );
    }

}
