package uk.gov.cslearning.catalogue.service.upload;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class InputStreamFactoryTest {
    private InputStreamFactory inputStreamFactory = new InputStreamFactory();

    @Test
    public void shouldReturnZipInputStream() throws IOException {
        InputStream inputStream = mock(InputStream.class);

        assertNotNull(inputStreamFactory.createZipInputStream(inputStream));
    }
}