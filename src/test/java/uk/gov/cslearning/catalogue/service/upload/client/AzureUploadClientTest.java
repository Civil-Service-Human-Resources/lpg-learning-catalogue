package uk.gov.cslearning.catalogue.service.upload.client;

import com.microsoft.azure.storage.ResultSegment;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;
import org.junit.Test;
import uk.gov.cslearning.catalogue.dto.upload.UploadStatus;
import uk.gov.cslearning.catalogue.dto.upload.UploadableFile;
import uk.gov.cslearning.catalogue.dto.upload.UploadedFile;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class AzureUploadClientTest {

    private final CloudBlobContainer container = mock(CloudBlobContainer.class);
    private final CloudBlockBlob blob = mock(CloudBlockBlob.class);
    private final BlobProperties blobProperties = mock(BlobProperties.class);

    private final String filePath = "test-file-path/text.txt";
    private final String contentType = "application/octet-stream";

    private UploadableFile generateFileUpload() {
        UploadableFile uploadableFile = mock(UploadableFile.class);
        when(uploadableFile.getFullPath()).thenReturn(filePath);
        when(uploadableFile.getContentType()).thenReturn(contentType);
        long fileSize = 2048;
        when(uploadableFile.getFileSize()).thenReturn(fileSize);
        when(uploadableFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[2048]));
        return uploadableFile;
    }

    @Test
    public void uploadShouldUploadAndReturnUploadedFile() throws Exception {

        UploadableFile uploadableFile = generateFileUpload();

        when(container.getBlockBlobReference(filePath)).thenReturn(blob);
        when(blob.getProperties()).thenReturn(blobProperties);

        UploadedFile result = new AzureUploadClient(container).upload(uploadableFile);

        assertEquals(2, result.getSizeKB());
        assertEquals(result.getPath(), filePath);
        assertEquals(UploadStatus.SUCCESS, result.getStatus());
        assertNull(result.getException());

        verify(blobProperties).setContentType(contentType);
    }

    @Test
    public void shouldAddURISyntaxExceptionToUploadedFile() throws URISyntaxException, StorageException {

        UploadableFile uploadableFile = generateFileUpload();
        URISyntaxException exception = mock(URISyntaxException.class);

        doThrow(exception).when(container).getBlockBlobReference(filePath);

        UploadedFile result = new AzureUploadClient(container).upload(uploadableFile);

        assertEquals(2, result.getSizeKB());
        assertEquals(result.getPath(), filePath);
        assertEquals(UploadStatus.FAIL, result.getStatus());
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
        CloudBlobContainer container = mock(CloudBlobContainer.class);
        CloudBlobDirectory directory = mock(CloudBlobDirectory.class);
        CloudBlobDirectory subDirectory = mock(CloudBlobDirectory.class);
        CloudBlockBlob blob = mock(CloudBlockBlob.class);
        @SuppressWarnings("unchecked")
        ResultSegment<ListBlobItem> resultSegment = (ResultSegment<ListBlobItem>) mock(ResultSegment.class);
        ArrayList<ListBlobItem> directoryContents = new ArrayList<>();
        directoryContents.add(blob);
        directoryContents.add(subDirectory);
        ArrayList<ListBlobItem> subDirectoryContents = new ArrayList<>();
        subDirectoryContents.add(blob);
        when(container.getDirectoryReference(dirPath)).thenReturn(directory);
        when(container.getDirectoryReference(subPath)).thenReturn(subDirectory);
        when(directory.listBlobsSegmented()).thenReturn(resultSegment);
        when(subDirectory.listBlobsSegmented()).thenReturn(resultSegment);
        when(resultSegment.getResults()).thenReturn(directoryContents).thenReturn(subDirectoryContents);
        when(blob.deleteIfExists()).thenReturn(true);
        when(subDirectory.getUri()).thenReturn(new URI(subPath));
        new AzureUploadClient(container).deleteDirectory(dirPath);
        verify(blob, times(2)).deleteIfExists();
    }
}
