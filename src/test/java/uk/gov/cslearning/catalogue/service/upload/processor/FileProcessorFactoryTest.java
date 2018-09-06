package uk.gov.cslearning.catalogue.service.upload.processor;

import org.junit.Test;
import uk.gov.cslearning.catalogue.dto.FileUpload;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class FileProcessorFactoryTest {
    private FileProcessorFactory fileProcessorFactory = new FileProcessorFactory();

    @Test
    public void shouldReturnDefaultFileProcessor() {
        FileUpload fileUpload = mock(FileUpload.class);
        FileProcessor processor = fileProcessorFactory.create(fileUpload);

        assertTrue(processor instanceof DefaultFileProcessor);
    }
}