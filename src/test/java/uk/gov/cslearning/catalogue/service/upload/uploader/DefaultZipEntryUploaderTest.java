package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.dto.UploadedFile;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;
import uk.gov.cslearning.catalogue.service.upload.processor.MetadataParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.zip.ZipEntry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultZipEntryUploaderTest {

    @Mock
    private MetadataParser metadataParser;

    @InjectMocks
    private DefaultZipEntryUploader uploader;

    @Test
    public void shouldUploadInputStreamAndReturnOptionalUploadedFile() throws IOException {
        UploadClient uploadClient = mock(UploadClient.class);
        String path = "test-path";
        String filename = "file.txt";
        String contentType = "text/plain";
        InputStream inputStream = mock(InputStream.class);

        byte[] buffer = new byte[1024];

        ZipEntry zipEntry = mock(ZipEntry.class);

        when(zipEntry.isDirectory()).thenReturn(false);
        when(zipEntry.getName()).thenReturn(filename);

        when(metadataParser.getContentType(inputStream, filename)).thenReturn(contentType);

        when(inputStream.read(buffer)).thenReturn(1024).thenReturn(0);

        UploadedFile uploadedFile = mock(UploadedFile.class);
        when(uploadClient.upload(inputStream, path, 1024, contentType)).thenReturn(uploadedFile);

        Optional<UploadedFile> result = uploader.upload(uploadClient, zipEntry, inputStream, path);

        assertTrue(result.isPresent());
        assertEquals(uploadedFile, result.get());
    }

    @Test
    public void shouldNotUploadDirectoryEntries() throws IOException, URISyntaxException {
        UploadClient uploadClient = mock(UploadClient.class);
        String path = "test-path";
        InputStream inputStream = mock(InputStream.class);

        ZipEntry zipEntry = mock(ZipEntry.class);
        when(zipEntry.isDirectory()).thenReturn(true);

        uploader.upload(uploadClient, zipEntry, inputStream, path);

        verifyZeroInteractions(uploadClient);
        verifyZeroInteractions(inputStream);
    }
}