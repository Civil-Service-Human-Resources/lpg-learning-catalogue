package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.gov.cslearning.catalogue.dto.UploadedFile;
import uk.gov.cslearning.catalogue.service.upload.InputStreamFactory;
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

@RunWith(PowerMockRunner.class)
@PrepareForTest(IOUtils.class)
public class DefaultZipEntryUploaderTest {

    @Mock
    private MetadataParser metadataParser;

    @Mock
    private InputStreamFactory inputStreamFactory;

    @InjectMocks
    private DefaultZipEntryUploader uploader;

    @Test
    public void shouldUploadInputStreamAndReturnOptionalUploadedFile() throws IOException {
        UploadClient uploadClient = mock(UploadClient.class);
        String path = "test-path";
        String filename = "file.txt";
        String contentType = "text/plain";
        InputStream zipEntryInputStream = mock(InputStream.class);
        InputStream byteArrayInputStream1 = mock(InputStream.class);
        InputStream byteArrayInputStream2 = mock(InputStream.class);

        byte[] bytes = "Hello World!".getBytes();

        PowerMockito.mockStatic(IOUtils.class);
        when(IOUtils.toByteArray(zipEntryInputStream)).thenReturn(bytes);

        ZipEntry zipEntry = mock(ZipEntry.class);

        when(zipEntry.isDirectory()).thenReturn(false);

        when(inputStreamFactory.createByteArrayInputStream(bytes))
                .thenReturn(byteArrayInputStream1)
                .thenReturn(byteArrayInputStream2);

        when(zipEntry.getName()).thenReturn(filename);

        when(metadataParser.getContentType(byteArrayInputStream1, filename)).thenReturn(contentType);

        UploadedFile uploadedFile = mock(UploadedFile.class);
        when(uploadClient.upload(byteArrayInputStream2, path, 12, contentType)).thenReturn(uploadedFile);

        Optional<UploadedFile> result = uploader.upload(uploadClient, zipEntry, zipEntryInputStream, path);

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