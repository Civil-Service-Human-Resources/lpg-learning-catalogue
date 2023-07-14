package uk.gov.cslearning.catalogue.service.upload.processor.metadata;

import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Service;

@Service
public class MetadataFactory {

    public Metadata getMetadata() {
        return new Metadata();
    }
}
