package uk.gov.cslearning.catalogue.service.upload.processor;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProcessedFileMetadataKeysTest {
    @Test
    public void toStringShouldReturnValue() {
        assertEquals("duration", ProcessedFileMetadataKeys.DURATION.toString());
        assertEquals("imageWidth", ProcessedFileMetadataKeys.IMAGE_WIDTH.toString());
        assertEquals("imageHeight", ProcessedFileMetadataKeys.IMAGE_HEIGHT.toString());
    }
}