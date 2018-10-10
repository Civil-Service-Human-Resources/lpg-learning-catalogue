package uk.gov.cslearning.catalogue.service.upload;

import org.junit.Test;

import java.io.InputStream;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;

public class FileFactoryTest {

    private final FileFactory fileFactory = new FileFactory();

    @Test
    public void shouldReturnFileFromPath() throws URISyntaxException {
        InputStream file = fileFactory.get("/application.yml");
        assertTrue(file != null);
    }
}