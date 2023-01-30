package uk.gov.cslearning.catalogue.service.upload.client;

import com.microsoft.azure.storage.ResultSegment;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.gov.cslearning.catalogue.dto.upload.UploadStatus;
import uk.gov.cslearning.catalogue.dto.upload.UploadableFile;
import uk.gov.cslearning.catalogue.dto.upload.UploadedFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.doThrow;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CloudBlobContainer.class, CloudBlockBlob.class, BlobProperties.class, CloudBlobDirectory.class})
public class AzureUploadClientTest {

    private CloudBlobContainer container = PowerMockito.mock(CloudBlobContainer.class);
    private CloudBlockBlob blob = PowerMockito.mock(CloudBlockBlob.class);
    private BlobProperties blobProperties = PowerMockito.mock(BlobProperties.class);
    private AzureUploadClient azureUploadClient;

    private final String filePath = "test-file-path/text.txt";
    private final String contentType = "application/octet-stream";
    private final int fileSize = 2048;

    private UploadableFile generateFileUpload(InputStream inputStream) throws IOException {
        UploadableFile uploadableFile = mock(UploadableFile.class);
        when(uploadableFile.getFullPath()).thenReturn(filePath);
        when(uploadableFile.getContentType()).thenReturn(contentType);
        when(uploadableFile.getBytes()).thenReturn(new byte[fileSize]);
        return uploadableFile;
    }

    @Test
    public void uploadShouldUploadAndReturnUploadedFile() throws Exception {

        InputStream inputStream = mock(InputStream.class);
        UploadableFile uploadableFile = generateFileUpload(inputStream);

        when(container.getBlockBlobReference(filePath)).thenReturn(blob);
        when(blob.getProperties()).thenReturn(blobProperties);

        UploadedFile result = new AzureUploadClient(container).upload(uploadableFile);

        assertEquals(result.getSizeKB(), 2);
        assertEquals(result.getPath(), filePath);
        assertEquals(result.getStatus(), UploadStatus.SUCCESS);
        assertNull(result.getException());

        verify(blob).upload(inputStream, fileSize);
        verify(blobProperties).setContentType(contentType);
    }

    @Test
    public void shouldAddURISyntaxExceptionToUploadedFile() throws URISyntaxException, StorageException, IOException {

        InputStream inputStream = mock(InputStream.class);
        UploadableFile uploadableFile = generateFileUpload(inputStream);
        URISyntaxException exception = mock(URISyntaxException.class);

        doThrow(exception).when(container).getBlockBlobReference(filePath);

        UploadedFile result = new AzureUploadClient(container).upload(uploadableFile);

        assertEquals(result.getSizeKB(), 2);
        assertEquals(result.getPath(), filePath);
        assertEquals(result.getStatus(), UploadStatus.FAIL);
        assertEquals(result.getException(), exception);
    }

    @Test
    public void shouldDeleteBlob() throws StorageException, URISyntaxException {
        when(container.getBlockBlobReference(filePath)).thenReturn(blob);
        when(blob.deleteIfExists()).thenReturn(true);
        new AzureUploadClient(container).delete(filePath);
        verify(container).getBlockBlobReference(filePath);
        verify(blob).deleteIfExists();
    }

    @Test
    public void shouldDeleteDirectory() throws StorageException, URISyntaxException {
        String dirPath = "path/to/dir";
        String subPath = "path/to/dir/file.pdf";
        CloudBlobContainer container = PowerMockito.mock(CloudBlobContainer.class);
        CloudBlobDirectory directory = PowerMockito.mock(CloudBlobDirectory.class);
        CloudBlobDirectory subDirectory = PowerMockito.mock(CloudBlobDirectory.class);
        CloudBlockBlob blob = PowerMockito.mock(CloudBlockBlob.class);
        ResultSegment<ListBlobItem> resultSegment = PowerMockito.mock(ResultSegment.class);
        ArrayList<ListBlobItem> directoryContents = new ArrayList<>();
        directoryContents.add(blob);
        directoryContents.add(subDirectory);
        ArrayList<ListBlobItem> subDirectoryContents = new ArrayList<>();
        subDirectoryContents.add(blob);
        PowerMockito.when(container.getDirectoryReference(dirPath)).thenReturn(directory);
        PowerMockito.when(container.getDirectoryReference(subPath)).thenReturn(subDirectory);
        PowerMockito.when(directory.listBlobsSegmented()).thenReturn(resultSegment);
        PowerMockito.when(subDirectory.listBlobsSegmented()).thenReturn(resultSegment);
        PowerMockito.when(resultSegment.getResults()).thenReturn(directoryContents).thenReturn(subDirectoryContents);
        PowerMockito.when(blob.deleteIfExists()).thenReturn(true);
        PowerMockito.when(subDirectory.getUri()).thenReturn(new URI(subPath));
        new AzureUploadClient(container).deleteDirectory(dirPath);
        verify(blob, times(2)).deleteIfExists();
    }
}
