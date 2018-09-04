package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.junit.Test;
import uk.gov.cslearning.catalogue.dto.UploadedFile;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.zip.ZipEntry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultZipEntryUploaderTest {

    private final ZipEntryUploader uploader = new DefaultZipEntryUploader();

    @Test
    public void shouldUploadInputStreamAndReturnOptionalUploadedFile() throws IOException, URISyntaxException {
        UploadClient uploadClient = mock(UploadClient.class);
        String path = "test-path";
        InputStream inputStream = mock(InputStream.class);
        byte[] buffer = new byte[1024];

        ZipEntry zipEntry = mock(ZipEntry.class);

        when(inputStream.read(buffer)).thenReturn(1024).thenReturn(0);

        UploadedFile uploadedFile = mock(UploadedFile.class);
        when(uploadClient.upload(inputStream, path, 1024)).thenReturn(uploadedFile);

        Optional<UploadedFile> result = uploader.upload(uploadClient, zipEntry, inputStream, path);

        assertTrue(result.isPresent());
        assertEquals(uploadedFile, result.get());
    }
}