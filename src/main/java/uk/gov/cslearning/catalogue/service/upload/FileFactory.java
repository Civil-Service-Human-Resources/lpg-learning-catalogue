package uk.gov.cslearning.catalogue.service.upload;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URISyntaxException;

@Component
public class FileFactory {
    public InputStream get(String path) throws URISyntaxException {
        return getClass().getResourceAsStream(path);
    }
}
