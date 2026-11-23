package uk.gov.cslearning.catalogue.service.upload.processor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.dto.upload.FileUpload;
import uk.gov.cslearning.catalogue.dto.upload.ProcessedFileUpload;
import uk.gov.cslearning.catalogue.dto.upload.UploadableFile;
import uk.gov.cslearning.catalogue.exception.FileProcessingException;
import uk.gov.cslearning.catalogue.service.upload.UploadableFileFactory;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultFileProcessorTest {
    @Mock
    private UploadableFileFactory uploadableFileFactory;
    @InjectMocks
    private DefaultFileProcessor defaultFileProcessor;

    @Test
    public void processShouldReturnProcessedFile() throws IOException {
        FileUpload fileUpload = mock(FileUpload.class);
        when(uploadableFileFactory.createFromFileUpload(fileUpload)).thenReturn(mock(UploadableFile.class));
        ProcessedFileUpload processedFile = defaultFileProcessor.process(fileUpload);
        assertEquals(fileUpload, processedFile.getFileUpload());
    }

    @Test(expected = FileProcessingException.class)
    public void processShouldThrowFileProcessingException() throws IOException {
        FileUpload fileUpload = mock(FileUpload.class);
        when(uploadableFileFactory.createFromFileUpload(fileUpload)).thenThrow(IOException.class);
        defaultFileProcessor.process(fileUpload);
    }
}
