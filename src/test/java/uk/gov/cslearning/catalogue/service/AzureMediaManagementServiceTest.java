package uk.gov.cslearning.catalogue.service;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.cslearning.catalogue.domain.media.Media;
import uk.gov.cslearning.catalogue.domain.media.MediaEntity;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.factory.MediaFactory;
import uk.gov.cslearning.catalogue.exception.FileUploadException;
import uk.gov.cslearning.catalogue.repository.MediaRepository;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AzureMediaManagementServiceTest {
    @Mock
    private CloudBlobClient client;

    @Mock
    private MediaFactory mediaFactory;

    @Mock
    private MediaRepository mediaRepository;

    private final String storageContainerName = "testContainer";

    private AzureMediaManagementService mediaManagementService;

    @Before
    public void setUp() {
        mediaManagementService = new AzureMediaManagementService(client, mediaFactory, mediaRepository, storageContainerName);
    }

    @Test
    public void shouldUploadFileAndReturnMedia() throws URISyntaxException, StorageException, IOException {
        String fileUploadContainer = "file-container";
        String fileName = "file-name";
        long fileSize = 78349873;

        FileUpload fileUpload = mock(FileUpload.class);
        when(fileUpload.getContainer()).thenReturn(fileUploadContainer);
        when(fileUpload.getName()).thenReturn(fileName);

        CloudBlobContainer container = mock(CloudBlobContainer.class);

        when(client.getContainerReference(storageContainerName)).thenReturn(container);

        CloudBlockBlob blob = mock(CloudBlockBlob.class);

        when(container.getBlockBlobReference(String.join("/", fileUploadContainer, fileName))).thenReturn(blob);
        MultipartFile multipartFile = mock(MultipartFile.class);

        when(fileUpload.getFile()).thenReturn(multipartFile);

        InputStream inputStream = mock(InputStream.class);

        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(multipartFile.getSize()).thenReturn(fileSize);

        MediaEntity media = mock(MediaEntity.class);
        when(mediaFactory.create(fileUpload)).thenReturn(media);

        when(mediaRepository.save(media)).thenReturn(media);

        Media result = mediaManagementService.create(fileUpload);

        assertEquals(media, result);
        verify(container).createIfNotExists();
        verify(blob).upload(inputStream, fileSize);
    }

    @Test
    public void shouldCatchUriSyntaxExceptionAndThrowFileUploadException() throws URISyntaxException, StorageException {
        FileUpload fileUpload = mock(FileUpload.class);

        URISyntaxException exception = mock(URISyntaxException.class);

        doThrow(exception).when(client).getContainerReference(storageContainerName);

        try {
            mediaManagementService.create(fileUpload);
            fail("Expected FileUploadException");
        } catch (FileUploadException e) {
            assertEquals(exception, e.getCause());
        }
    }

    @Test
    public void shouldCatchStorageExceptionAndThrowFileUploadException() throws URISyntaxException, StorageException {
        String fileuploadContainer = "test-container";

        FileUpload fileUpload = mock(FileUpload.class);
        when(fileUpload.getContainer()).thenReturn(fileuploadContainer);

        StorageException exception = mock(StorageException.class);

        doThrow(exception).when(client).getContainerReference(storageContainerName);

        try {
            mediaManagementService.create(fileUpload);
            fail("Expected FileUploadException");
        } catch (FileUploadException e) {
            assertEquals(exception, e.getCause());
        }
    }

    @Test
    public void shouldCatchIOExceptionAndThrowFileUploadException() throws URISyntaxException, StorageException, IOException {
        String fileUploadContainer = "test-container";
        String fileName = "file-name";

        FileUpload fileUpload = mock(FileUpload.class);
        when(fileUpload.getContainer()).thenReturn(fileUploadContainer);
        when(fileUpload.getName()).thenReturn(fileName);

        CloudBlobContainer container = mock(CloudBlobContainer.class);

        when(client.getContainerReference(storageContainerName)).thenReturn(container);

        CloudBlockBlob blob = mock(CloudBlockBlob.class);

        when(container.getBlockBlobReference(String.join("/", fileUploadContainer, fileName))).thenReturn(blob);
        MultipartFile multipartFile = mock(MultipartFile.class);

        when(fileUpload.getFile()).thenReturn(multipartFile);

        IOException exception = mock(IOException.class);

        doThrow(exception).when(multipartFile).getInputStream();

        try {
            mediaManagementService.create(fileUpload);
            fail("Expected FileUploadException");
        } catch (FileUploadException e) {
            assertEquals(exception, e.getCause());
        }
    }
}