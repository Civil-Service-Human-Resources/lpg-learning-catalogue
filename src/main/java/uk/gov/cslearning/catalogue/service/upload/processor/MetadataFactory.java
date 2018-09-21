package uk.gov.cslearning.catalogue.service.upload.processor;

import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Component;

@Component
public class MetadataFactory {
    public Metadata create() {
        return new Metadata();
    }
}
