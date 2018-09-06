package uk.gov.cslearning.catalogue.service.upload.processor.mp4;

import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Component;

@Component
public class MetadataFactory {
    public Metadata create() {
        return new Metadata();
    }
}
