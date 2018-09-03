package uk.gov.cslearning.catalogue.service.upload;

import java.io.File;
import java.net.URISyntaxException;

public class FileFactory {
    public File get(String path) throws URISyntaxException {
        return new File(getClass().getResource(path).toURI());
    }
}
