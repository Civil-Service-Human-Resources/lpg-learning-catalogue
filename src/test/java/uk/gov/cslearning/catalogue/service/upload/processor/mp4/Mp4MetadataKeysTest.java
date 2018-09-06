package uk.gov.cslearning.catalogue.service.upload.processor.mp4;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Mp4MetadataKeysTest {
    @Test
    public void toStringshouldReturnValue() {
        assertEquals("xmpDM:duration", Mp4MetadataKeys.DURATION.toString());
        assertEquals("tiff:ImageWidth", Mp4MetadataKeys.IMAGE_WIDTH.toString());
        assertEquals("tiff:ImageLength", Mp4MetadataKeys.IMAGE_HEIGHT.toString());
    }
}