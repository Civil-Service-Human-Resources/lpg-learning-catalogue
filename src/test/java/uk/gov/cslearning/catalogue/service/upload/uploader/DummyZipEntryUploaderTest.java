package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.junit.Test;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.zip.ZipEntry;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

public class DummyZipEntryUploaderTest {

    ZipEntryUploader uploader = new DummyZipEntryUploader();

    @Test
    public void shouldReturnEmptyOptional() throws IOException, URISyntaxException {
        UploadClient uploadClient = mock(UploadClient.class);
        ZipEntry zipEntry = mock(ZipEntry.class);
        InputStream inputStream = mock(InputStream.class);
        String path = "test-path";

        assertFalse(uploader.upload(uploadClient, zipEntry, inputStream, path).isPresent());
    }
}