package uk.gov.cslearning.catalogue.config;

import com.google.common.collect.ImmutableMap;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.gov.cslearning.catalogue.domain.media.MediaEntity;
import uk.gov.cslearning.catalogue.domain.media.factory.CreateDocumentFunction;
import uk.gov.cslearning.catalogue.domain.media.factory.CreateScormFunction;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.service.upload.processor.DefaultFileProcessor;
import uk.gov.cslearning.catalogue.service.upload.processor.FileProcessor;
import uk.gov.cslearning.catalogue.service.upload.processor.mp4.Mp4FileProcessor;
import uk.gov.cslearning.catalogue.service.upload.uploader.DefaultUploader;
import uk.gov.cslearning.catalogue.service.upload.uploader.ScormUploader;
import uk.gov.cslearning.catalogue.service.upload.uploader.Uploader;

import java.util.Map;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CloudBlobClient.class, CloudStorageAccount.class})
public class UploadConfigTest {

    private final UploadConfig config = new UploadConfig();

    @Test
    public void shouldHaveCorrectUploaderMappings() {
        DefaultUploader defaultUploader = mock(DefaultUploader.class);
        ScormUploader scormUploader = mock(ScormUploader.class);

        Map<String, Uploader> uploaderMap = ImmutableMap.<String, Uploader>builder()
                .put("doc", defaultUploader)
                .put("docx", defaultUploader)
                .put("pdf", defaultUploader)
                .put("ppsm", defaultUploader)
                .put("ppt", defaultUploader)
                .put("pptx", defaultUploader)
                .put("xls", defaultUploader)
                .put("xlsx", defaultUploader)
                .put("zip", scormUploader)
                .build();

        assertEquals(uploaderMap.keySet(), config.uploaderFactoryMethods(defaultUploader, scormUploader).keySet());

        uploaderMap.forEach((key, value) -> assertEquals(
                key, value, config.uploaderFactoryMethods(defaultUploader, scormUploader).get(key).get()));
    }

    @Test
    public void shouldHaveCorrectMediaEntityFactoryMappings() {
        CreateDocumentFunction createDocumentFunction = mock(CreateDocumentFunction.class);
        CreateScormFunction createScormFunction = mock(CreateScormFunction.class);

        Map<String, Function<Upload, MediaEntity>> functions = ImmutableMap.<String, Function<Upload, MediaEntity>>builder()
                .put("doc", createDocumentFunction)
                .put("docx", createDocumentFunction)
                .put("pdf", createDocumentFunction)
                .put("ppsm", createDocumentFunction)
                .put("ppt", createDocumentFunction)
                .put("pptx", createDocumentFunction)
                .put("xls", createDocumentFunction)
                .put("xlsx", createDocumentFunction)
                .put("zip", createScormFunction)
                .build();

        assertEquals(functions.keySet(), config.mediaEntityFactoryMethods(createDocumentFunction, createScormFunction).keySet());

        functions.forEach((key, value) -> assertEquals(key, value,
                config.mediaEntityFactoryMethods(createDocumentFunction, createScormFunction).get(key)));
    }

    @Test
    public void shouldHaveCorrectFileSubstitutionMap() {
        Map<String, String> substitutionMap = ImmutableMap.of(
                "js/player_management/close_methods.js", "/file-substitutions/close_methods.js",
                "js/player_management/content_tracking/adapters/tincan_wrapper.js", "/file-substitutions/tincan_wrapper.js",
                "js/player_management/portal_overrides.js", "/file-substitutions/portal_overrides.js",
                "story_content/user.js", "/file-substitutions/user.js",
                "SCORMDriver/Configuration.js", "/file-substitutions/Configuration.js"
        );

        assertEquals(substitutionMap.keySet(), config.fileSubstitions().keySet());
        substitutionMap.forEach((key, value) -> assertEquals(key, value, config.fileSubstitions().get(key)));
    }


    @Test
    public void storageClientShouldReturnCloudBlobClient() {
        CloudStorageAccount cloudStorageAccount = PowerMockito.mock(CloudStorageAccount.class);
        CloudBlobClient cloudBlobClient = PowerMockito.mock(CloudBlobClient.class);

        PowerMockito.when(cloudStorageAccount.createCloudBlobClient()).thenReturn(cloudBlobClient);

        assertEquals(cloudBlobClient, config.storageClient(cloudStorageAccount));
    }


    @Test
    public void shouldHaveCorrectFileProcessorMap() {
        DefaultFileProcessor defaultFileProcessor = mock(DefaultFileProcessor.class);
        Mp4FileProcessor mp4FileProcessor = mock(Mp4FileProcessor.class);

        Map<String, FileProcessor> fileProcessorMap = ImmutableMap.<String, FileProcessor>builder()
                .put("doc",  defaultFileProcessor) // MS Word
                .put("docx", defaultFileProcessor) // MS Word
                .put("pdf",  defaultFileProcessor) // PDF
                .put("ppsm", defaultFileProcessor) // MS PowerPoint
                .put("ppt",  defaultFileProcessor) // MS PowerPoint
                .put("pptx", defaultFileProcessor) // MS PowerPoint
                .put("xls",  defaultFileProcessor) // MS Excel
                .put("xlsx", defaultFileProcessor) // MS Excel
                .put("zip",  defaultFileProcessor) // Scorm
                .put("mp4",  mp4FileProcessor)     // Video
                .build();

        assertEquals(fileProcessorMap.keySet(),
                config.fileProcessorMap(defaultFileProcessor, mp4FileProcessor).keySet());


        fileProcessorMap.forEach((key, value) -> assertEquals(key, value,
                config.fileProcessorMap(defaultFileProcessor, mp4FileProcessor).get(key)));
    }
}