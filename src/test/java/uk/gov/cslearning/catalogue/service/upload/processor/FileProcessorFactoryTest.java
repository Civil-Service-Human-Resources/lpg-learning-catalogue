package uk.gov.cslearning.catalogue.service.upload.processor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.exception.UnknownFileTypeException;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileProcessorFactoryTest {

    @Mock
    Map<String, FileProcessor> fileProcessorMap;

    @InjectMocks
    private FileProcessorFactory fileProcessorFactory;

    @Test
    public void shouldReturnDefaultFileProcessor() {
        String fileExtension = "xxx";
        FileProcessor fileProcessor = mock(FileProcessor.class);
        FileUpload fileUpload = mock(FileUpload.class);
        when(fileUpload.getExtension()).thenReturn(fileExtension);

        when(fileProcessorMap.containsKey(fileExtension)).thenReturn(true);
        when(fileProcessorMap.get(fileExtension)).thenReturn(fileProcessor);

        assertEquals(fileProcessor, fileProcessorFactory.create(fileUpload));
    }

    @Test
    public void shouldThrowUnknownFileTypeExceptionIfExtensionNotFound() {
        String fileExtension = "xxx";
        FileUpload fileUpload = mock(FileUpload.class);
        when(fileUpload.getExtension()).thenReturn(fileExtension);

        when(fileProcessorMap.containsKey(fileExtension)).thenReturn(false);

        try {
            fileProcessorFactory.create(fileUpload);
            fail("Expected UnknownFileTypeException");
        } catch (UnknownFileTypeException e) {
            assertTrue(e.getMessage().contains("Uploaded file has an unknown extension: xxx Mock for FileUpload, hashCode: "));
        }
    }
}