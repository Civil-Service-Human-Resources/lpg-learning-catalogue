package uk.gov.cslearning.catalogue.service.upload.processor;

import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.exception.FileUploadException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class MetadataParser {
    private final Tika tika;
    private final MetadataFactory metadataFactory;

    public MetadataParser(Tika tika, MetadataFactory metadataFactory) {
        this.tika = tika;
        this.metadataFactory = metadataFactory;
    }

    public Map<String, String> parse(InputStream inputStream) {
        Metadata metadata = metadataFactory.create();

        try {
            tika.parse(inputStream, metadata);

            Map<String, String> data = new HashMap<>();
            Arrays.stream(metadata.names()).forEach(k -> data.put(k, metadata.get(k)));

            return data;
        } catch (IOException e) {
            throw new FileUploadException(e);
        }
    }

    public String getContentType(InputStream inputStream, String filename) {
        try {
            return tika.detect(inputStream, filename);
        } catch (IOException e) {
            throw new FileUploadException(e);
        }
    }
}
