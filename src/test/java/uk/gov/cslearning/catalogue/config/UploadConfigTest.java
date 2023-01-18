package uk.gov.cslearning.catalogue.config;

import com.google.common.collect.ImmutableMap;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import org.apache.tika.Tika;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.gov.cslearning.catalogue.service.upload.processor.DefaultFileProcessor;
import uk.gov.cslearning.catalogue.service.upload.processor.FileProcessor;
import uk.gov.cslearning.catalogue.service.upload.processor.Mp4FileProcessor;
import uk.gov.cslearning.catalogue.service.upload.uploader.DefaultUploader;
import uk.gov.cslearning.catalogue.service.upload.uploader.ScormUploader;
import uk.gov.cslearning.catalogue.service.upload.uploader.Uploader;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
                .put("mp4", defaultUploader)
                .put("zip", scormUploader)
                .put("jpg", defaultUploader)
                .put("jpeg", defaultUploader)
                .put("png", defaultUploader)
                .put("svg", defaultUploader)
                .build();

        assertEquals(uploaderMap.keySet(), config.uploaderMap(defaultUploader, scormUploader).keySet());

        uploaderMap.forEach((key, value) -> assertEquals(
                key, value, config.uploaderMap(defaultUploader, scormUploader).get(key)));
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

    @Test
    public void tikaShouldReturnTika() {
        Tika tika = config.tika();
        assertNotNull(tika);
    }

    @Test
    public void shouldReturnDocumentBuilderFatory() {
        DocumentBuilderFactory documentBuilderFactory = config.documentBuilderFactory();
        assertNotNull(documentBuilderFactory);
    }

}
