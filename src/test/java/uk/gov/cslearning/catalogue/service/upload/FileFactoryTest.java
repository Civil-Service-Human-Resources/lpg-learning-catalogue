package uk.gov.cslearning.catalogue.service.upload;

import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

public class FileFactoryTest {

    private final FileFactory fileFactory = new FileFactory();

    @Test
    public void shouldReturnFileInputStreamFromPath() {
        InputStream fileInputStream = fileFactory.getInputStreamFromPath("/application.yml");
        assertNotNull(fileInputStream);
    }
}