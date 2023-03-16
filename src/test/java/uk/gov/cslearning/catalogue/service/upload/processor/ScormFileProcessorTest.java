package uk.gov.cslearning.catalogue.service.upload.processor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.domain.CustomMediaMetadata;
import uk.gov.cslearning.catalogue.dto.upload.FileUpload;
import uk.gov.cslearning.catalogue.dto.upload.ProcessedFileUpload;
import uk.gov.cslearning.catalogue.dto.upload.UploadableFile;
import uk.gov.cslearning.catalogue.exception.FileProcessingException;
import uk.gov.cslearning.catalogue.service.upload.UploadableFileFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ScormFileProcessorTest {

    @Mock
    private UploadableFileFactory uploadableFileFactory;
    @Mock
    private ELearningManifestService eLearningManifestService;
    @InjectMocks
    private ScormFileProcessor scormFileProcessor;

    private UploadableFile generateMockUploadableFile(String filename) {
        UploadableFile uploadableFile = mock(UploadableFile.class);
        when(uploadableFile.getName()).thenReturn(filename);
        return uploadableFile;
    }

    @Test
    public void testCreateProcessedFileUpload() throws IOException {
        List<UploadableFile> uploadableFiles = new ArrayList<>();
        uploadableFiles.add(generateMockUploadableFile("TestFile.txt"));
        uploadableFiles.add(generateMockUploadableFile("TestFile.txt"));
        uploadableFiles.add(generateMockUploadableFile("imsmanifest.xml"));
        FileUpload fileUpload = mock(FileUpload.class);
        when(uploadableFileFactory.createFromZip(fileUpload)).thenReturn(uploadableFiles);
        when(eLearningManifestService.fetchManifestFromFileList(Arrays.asList("TestFile.txt",
                "TestFile.txt",
                "imsmanifest.xml"))).thenReturn("imsmanifest.xml");
        ProcessedFileUpload processedFileUpload = scormFileProcessor.process(fileUpload);
        assertEquals("File has incorrect manifest", "imsmanifest.xml", processedFileUpload.getMetadata().get(CustomMediaMetadata.ELEARNING_MANIFEST.getMetadataKey()));
        assertEquals("File upload was not stored correctly", fileUpload, processedFileUpload.getFileUpload());
        assertEquals("Incorrect number of uploadableFiles", 3, processedFileUpload.getUploadableFiles().size());
    }

    @Test(expected = FileProcessingException.class)
    public void testMissingFileValidation() throws IOException {
        List<UploadableFile> uploadableFiles = new ArrayList<>();
        uploadableFiles.add(generateMockUploadableFile("TestFile.txt"));
        uploadableFiles.add(generateMockUploadableFile("TestFile.txt"));
        uploadableFiles.add(generateMockUploadableFile("TestFile.txt"));
        FileUpload fileUpload = mock(FileUpload.class);
        when(uploadableFileFactory.createFromZip(fileUpload)).thenReturn(uploadableFiles);
        when(eLearningManifestService.fetchManifestFromFileList(Arrays.asList("TestFile.txt",
                "TestFile.txt",
                "TestFile.txt"))).thenReturn(null);
        scormFileProcessor.process(fileUpload);
    }
}
