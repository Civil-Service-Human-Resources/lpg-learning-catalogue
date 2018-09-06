package uk.gov.cslearning.catalogue.service.upload.processor.mp4;

import org.apache.tika.metadata.Metadata;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class MetadataFactoryTest {
    @Test
    public void shouldReturnMetadata() {
        Metadata metadata = new MetadataFactory().create();
        assertNotNull(metadata);
    }
}