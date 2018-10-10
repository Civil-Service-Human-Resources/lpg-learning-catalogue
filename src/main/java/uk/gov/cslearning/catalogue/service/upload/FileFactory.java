package uk.gov.cslearning.catalogue.service.upload;

import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class FileFactory {
    public InputStream getInputStreamFromPath(String path) {
        return getClass().getResourceAsStream(path);
    }
}
