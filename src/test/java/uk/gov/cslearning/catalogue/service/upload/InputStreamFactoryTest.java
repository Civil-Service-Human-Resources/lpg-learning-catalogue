package uk.gov.cslearning.catalogue.service.upload;

import org.junit.Test;

import java.io.*;
import java.net.URISyntaxException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class InputStreamFactoryTest {
    private InputStreamFactory inputStreamFactory = new InputStreamFactory();

    @Test
    public void shouldReturnZipInputStream() throws IOException {
        InputStream inputStream = mock(InputStream.class);

        assertNotNull(inputStreamFactory.createZipInputStream(inputStream));
    }

    @Test
    public void shouldReturnFileInputStream() throws FileNotFoundException, URISyntaxException {
        File file = new File(getClass().getResource("/application.yml").toURI());

        assertNotNull(inputStreamFactory.createFileInputStream(file));
        assertTrue(inputStreamFactory.createFileInputStream(file) instanceof FileInputStream);
    }

    @Test
    public void shouldReturnByteArrayInputStream() throws IOException {
        byte[] bytes = "Hello World!".getBytes();

        InputStream inputStream = inputStreamFactory.createByteArrayInputStream(bytes);

        assertNotNull(inputStream);
        assertTrue(inputStream instanceof ByteArrayInputStream);
    }

}