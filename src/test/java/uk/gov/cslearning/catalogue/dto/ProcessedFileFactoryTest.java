package uk.gov.cslearning.catalogue.dto;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ProcessedFileFactoryTest {
    private final ProcessedFileFactory processedFileFactory = new ProcessedFileFactory();

    @Test
    public void shouldReturnProcessedFile() {
        FileUpload fileUpload = mock(FileUpload.class);
        Map<String, String> metadata = ImmutableMap.of("key", "value");

        ProcessedFile processedFile = processedFileFactory.create(fileUpload, metadata);

        assertEquals(fileUpload, processedFile.getFileUpload());
        assertEquals(metadata, processedFile.getMetadata());
    }
}