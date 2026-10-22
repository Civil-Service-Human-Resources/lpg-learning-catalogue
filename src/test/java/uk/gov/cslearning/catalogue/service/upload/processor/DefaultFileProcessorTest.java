package uk.gov.cslearning.catalogue.service.upload.processor;


import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.cslearning.catalogue.dto.upload.FileUpload;
import uk.gov.cslearning.catalogue.dto.upload.ProcessedFileUpload;
import uk.gov.cslearning.catalogue.dto.upload.UploadableFile;
import uk.gov.cslearning.catalogue.exception.FileProcessingException;
import uk.gov.cslearning.catalogue.service.upload.UploadableFileFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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

    @Test
    public void processShouldThrowFileProcessingException() throws IOException {
        FileUpload fileUpload = mock(FileUpload.class);
        when(uploadableFileFactory.createFromFileUpload(fileUpload)).thenThrow(IOException.class);
        assertThrows(FileProcessingException.class, () -> defaultFileProcessor.process(fileUpload));
    }
}
