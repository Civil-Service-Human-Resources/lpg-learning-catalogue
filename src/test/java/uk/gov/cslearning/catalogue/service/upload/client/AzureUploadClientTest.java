package uk.gov.cslearning.catalogue.service.upload.client;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.gov.cslearning.catalogue.exception.FileUploadException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CloudBlobClient.class, CloudBlobContainer.class, CloudBlockBlob.class})
public class AzureUploadClientTest {

    private final String storageContainerName = "storage-container-name";
    private final CloudBlobClient cloudBlobClient = PowerMockito.mock(CloudBlobClient.class);
    private AzureUploadClient azureUploadClient;

    @Before
    public void setUp() {
        azureUploadClient = new AzureUploadClient(cloudBlobClient, storageContainerName);
    }

    @Test
    public void uploadShouldUploadAndReturnUploadedFile() throws Exception {
        String filePath = "test-file-path";
        long fileSize = 99;
        InputStream inputStream = mock(InputStream.class);

        CloudBlobContainer container = PowerMockito.mock(CloudBlobContainer.class);
        CloudBlockBlob blob = PowerMockito.mock(CloudBlockBlob.class);

        PowerMockito.when(cloudBlobClient.getContainerReference(storageContainerName)).thenReturn(container);
        PowerMockito.when(container.getBlockBlobReference(filePath)).thenReturn(blob);

        azureUploadClient.upload(inputStream, filePath, fileSize);

        verify(blob).upload(inputStream, fileSize);
    }

    @Test
    public void shouldThrowFileUploadExceptionOnStorageException() throws URISyntaxException, StorageException {
        String filePath = "test-file-path";
        long fileSize = 99;
        InputStream inputStream = mock(InputStream.class);

        StorageException exception = mock(StorageException.class);

        doThrow(exception).when(cloudBlobClient).getContainerReference(storageContainerName);

        try {
            azureUploadClient.upload(inputStream, filePath, fileSize);
            fail("Expected FileUploadException");
        } catch (FileUploadException e) {
            assertEquals(exception, e.getCause());
        }
    }

    @Test
    public void shouldThrowFileUploadExceptionOnURISyntaxException() throws URISyntaxException, StorageException {
        String filePath = "test-file-path";
        long fileSize = 99;
        InputStream inputStream = mock(InputStream.class);

        URISyntaxException exception = mock(URISyntaxException.class);

        doThrow(exception).when(cloudBlobClient).getContainerReference(storageContainerName);

        try {
            azureUploadClient.upload(inputStream, filePath, fileSize);
            fail("Expected FileUploadException");
        } catch (FileUploadException e) {
            assertEquals(exception, e.getCause());
        }
    }

    @Test
    public void shouldThrowFileUploadExceptionOnIOException() throws URISyntaxException, StorageException, IOException {
        String filePath = "test-file-path";
        long fileSize = 99;
        InputStream inputStream = mock(InputStream.class);

        CloudBlobContainer container = PowerMockito.mock(CloudBlobContainer.class);
        CloudBlockBlob blob = PowerMockito.mock(CloudBlockBlob.class);

        PowerMockito.when(cloudBlobClient.getContainerReference(storageContainerName)).thenReturn(container);
        PowerMockito.when(container.getBlockBlobReference(filePath)).thenReturn(blob);

        IOException exception = mock(IOException.class);

        doThrow(exception).when(blob).upload(inputStream, fileSize);

        try {
            azureUploadClient.upload(inputStream, filePath, fileSize);
            fail("Expected FileUploadException");
        } catch (FileUploadException e) {
            assertEquals(exception, e.getCause());
        }
    }

}