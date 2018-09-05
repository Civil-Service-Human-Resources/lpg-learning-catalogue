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
        return ImmutableMap.<String, Supplier<Uploader>>builder()
                .put("doc",  () -> defaultUploader) // MS Word
                .put("docx", () -> defaultUploader) // MS Word
                .put("pdf",  () -> defaultUploader) // PDF
                .put("ppsm", () -> defaultUploader) // MS PowerPoint
                .put("ppt",  () -> defaultUploader) // MS PowerPoint
                .put("pptx", () -> defaultUploader) // MS PowerPoint
                .put("xls",  () -> defaultUploader) // MS Excel
                .put("xlsx", () -> defaultUploader) // MS Excel
                .put("zip",  () -> scormUploader)   // Scorm
                .build();

    }

    @Bean(name="mediaEntityFactoryMethods")
    public Map<String, Function<Upload, MediaEntity>> mediaEntityFactoryMethods(
            CreateDocumentFunction createDocumentFunction,
            CreateScormFunction createScormFunction
    ){
        return ImmutableMap.<String, Function<Upload, MediaEntity>>builder()
                .put("doc",  createDocumentFunction) // MS Word
                .put("docx", createDocumentFunction) // MS Word
                .put("pdf",  createDocumentFunction) // PDF
                .put("ppsm", createDocumentFunction) // MS PowerPoint
                .put("ppt",  createDocumentFunction) // MS PowerPoint
                .put("pptx", createDocumentFunction) // MS PowerPoint
                .put("xls",  createDocumentFunction) // MS Excel
                .put("xlsx", createDocumentFunction) // MS Excel
                .put("zip",  createScormFunction)    // Scorm
                .build();
    }

    @Bean("fileSubstitutions")
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
