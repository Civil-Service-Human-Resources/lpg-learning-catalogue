package uk.gov.cslearning.catalogue.service.upload.client;

import com.microsoft.azure.storage.ResultSegment;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;
import org.eclipse.jetty.util.IO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.gov.cslearning.catalogue.dto.UploadableFile;
import uk.gov.cslearning.catalogue.dto.upload.SuccessfulUploadedFile;
import uk.gov.cslearning.catalogue.dto.upload.UploadedFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.doThrow;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CloudBlobClient.class, CloudBlobContainer.class, CloudBlockBlob.class, BlobProperties.class, CloudBlobDirectory.class})
public class AzureUploadClientTest {
    private final CloudBlobContainer container = PowerMockito.mock(CloudBlobContainer.class);
    private AzureUploadClient azureUploadClient;

    @Before
    public void setUp() {
        azureUploadClient = new AzureUploadClient(container);
    }

    @Test
    public void uploadShouldUploadAndReturnUploadedFile() throws Exception {
        String contentType = "application/octet-stream";
        String destination = "test-file-path";
        String filename = "test.txt";
        String filePath = "test-file-path/text.txt";
        long fileSize = 99;
        InputStream inputStream = mock(InputStream.class);

        CloudBlockBlob blob = PowerMockito.mock(CloudBlockBlob.class);
        BlobProperties blobProperties = PowerMockito.mock(BlobProperties.class);

        PowerMockito.when(container.getBlockBlobReference(filePath)).thenReturn(blob);
        PowerMockito.when(blob.getProperties()).thenReturn(blobProperties);

        UploadableFile uploadableFile = new UploadableFile(filename, destination, inputStream, IO.readBytes(inputStream), contentType);
        UploadedFile uploadedFile = new SuccessfulUploadedFile(fileSize, filePath);

        UploadedFile result = azureUploadClient.upload(uploadableFile);

        assertEquals(uploadedFile, result);

        verify(blob).upload(inputStream, fileSize);
        verify(blobProperties).setContentType(contentType);
    }

    @Test
    public void uploadShouldSetContentType() throws Exception {
        String contentType = "application/mp4";
        String filePath = "test-file-path";
        long fileSize = 99;
        InputStream inputStream = mock(InputStream.class);

        CloudBlobContainer container = PowerMockito.mock(CloudBlobContainer.class);
        CloudBlockBlob blob = PowerMockito.mock(CloudBlockBlob.class);
        BlobProperties blobProperties = PowerMockito.mock(BlobProperties.class);

        PowerMockito.when(container.getBlockBlobReference(filePath)).thenReturn(blob);
        PowerMockito.when(blob.getProperties()).thenReturn(blobProperties);

        azureUploadClient.upload(inputStream, filePath, fileSize, contentType);

        verify(blobProperties).setContentType(contentType);
    }


    @Test
    public void shouldThrowFileUploadExceptionOnStorageException() {
        String filePath = "test-file-path";
        long fileSize = 99;
        InputStream inputStream = mock(InputStream.class);

        StorageException exception = mock(StorageException.class);

        UploadedFile uploadedFile = UploadedFile.createFailedUploadedFile(filePath, fileSize, exception);

        UploadedFile result = azureUploadClient.upload(inputStream, filePath, fileSize);
        assertEquals(uploadedFile, result);
    }

    @Test
    public void shouldAddURISyntaxExceptionToUploadedFile() throws URISyntaxException, StorageException {
        String filePath = "test-file-path";
        long fileSize = 99;
        InputStream inputStream = mock(InputStream.class);

        URISyntaxException exception = mock(URISyntaxException.class);

        doThrow(exception).when(container).getBlockBlobReference("Test");

        UploadedFile uploadedFile = UploadedFile.createFailedUploadedFile(filePath, fileSize, exception);

        UploadedFile result = azureUploadClient.upload(inputStream, filePath, fileSize);
        assertEquals(uploadedFile, result);
    }

    @Test
    public void shouldThrowFileUploadExceptionOnIOException() throws URISyntaxException, StorageException, IOException {
        String filePath = "test-file-path";
        long fileSize = 99;
        InputStream inputStream = mock(InputStream.class);

        CloudBlobContainer container = PowerMockito.mock(CloudBlobContainer.class);
        CloudBlockBlob blob = PowerMockito.mock(CloudBlockBlob.class);
        BlobProperties blobProperties = PowerMockito.mock(BlobProperties.class);

        PowerMockito.when(container.getBlockBlobReference(filePath)).thenReturn(blob);
        PowerMockito.when(blob.getProperties()).thenReturn(blobProperties);

        IOException exception = mock(IOException.class);

        doThrow(exception).when(blob).upload(inputStream, fileSize);

        UploadedFile uploadedFile = UploadedFile.createFailedUploadedFile(filePath, fileSize, exception);

        UploadedFile result = azureUploadClient.upload(inputStream, filePath, fileSize);
        assertEquals(uploadedFile, result);
    }

    @Test
    public void shouldDeleteBlob() throws StorageException, URISyntaxException {
        String filePath = "path/to/file.pdf";
        CloudBlobContainer container = PowerMockito.mock(CloudBlobContainer.class);
        CloudBlockBlob blob = PowerMockito.mock(CloudBlockBlob.class);
        PowerMockito.when(container.getBlockBlobReference(filePath)).thenReturn(blob);
        PowerMockito.when(blob.deleteIfExists()).thenReturn(true);
        azureUploadClient.delete(filePath);
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
        azureUploadClient.deleteDirectory(dirPath);
        verify(blob, times(2)).deleteIfExists();
    }
}
