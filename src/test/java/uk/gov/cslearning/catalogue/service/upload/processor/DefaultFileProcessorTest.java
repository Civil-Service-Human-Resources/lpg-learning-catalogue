package uk.gov.cslearning.catalogue.service.upload.processor;

import org.junit.Test;
import uk.gov.cslearning.catalogue.dto.upload.FileUpload;
import uk.gov.cslearning.catalogue.dto.upload.ProcessedFileUpload;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class DefaultFileProcessorTest {
    private final DefaultFileProcessor processor = new DefaultFileProcessor();

    @Test
    public void processShouldReturnProcessedFile() {

        FileUpload fileUpload = mock(FileUpload.class);

        ProcessedFileUpload processedFile = processor.process(fileUpload);

        assertEquals(fileUpload, processedFile.getFileUpload());
    }
}
