package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.upload.Upload;
import uk.gov.cslearning.catalogue.dto.upload.UploadedFile;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class DefaultUploaderTest {

    @InjectMocks
    private DefaultUploader uploader;

    @Test
    public void shouldUploadFileAndReturnUploadOnSuccessfulUpload() throws IOException {
        String containerName = "test-container";
        String id = "file-upload-id";
        String filename = "test.pdf";
        long fileSize = 99;
        String contentType = "application/pdf";

        String path = String.join("/", containerName, id, filename);
        FileUpload fileUpload = mock(FileUpload.class);
        ProcessedFile processedFile = mock(ProcessedFile.class);
        MultipartFile multipartFile = mock(MultipartFile.class);
        InputStream inputStream = mock(InputStream.class);

        when(processedFile.getFileUpload()).thenReturn(fileUpload);
        when(fileUpload.getContainer()).thenReturn(containerName);
        when(fileUpload.getId()).thenReturn(id);
        when(fileUpload.getFile()).thenReturn(multipartFile);
        when(fileUpload.getName()).thenReturn(filename);
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(multipartFile.getSize()).thenReturn(fileSize);
        when(multipartFile.getContentType()).thenReturn(contentType);


        UploadClient uploadClient = mock(UploadClient.class);
        UploadedFile uploadedFile = mock(UploadedFile.class);
        when(uploadClient.upload(inputStream, path, fileSize, contentType)).thenReturn(uploadedFile);

        Upload upload = Upload.createSuccessfulUpload(processedFile, Collections.singletonList(uploadedFile), path);
        Upload result = uploader.upload(processedFile);

        assertEquals(upload, result);
    }

    @Test
    public void shouldAddErrorToUploadOnIOException() throws IOException {
        String containerName = "test-container";
        String id = "file-upload-id";
        String filename = "test.pdf";

        String path = String.join("/", containerName, id, filename);
        FileUpload fileUpload = mock(FileUpload.class);
        ProcessedFile processedFile = mock(ProcessedFile.class);
        MultipartFile multipartFile = mock(MultipartFile.class);

        when(processedFile.getFileUpload()).thenReturn(fileUpload);
        when(fileUpload.getContainer()).thenReturn(containerName);
        when(fileUpload.getId()).thenReturn(id);
        when(fileUpload.getFile()).thenReturn(multipartFile);
        when(fileUpload.getName()).thenReturn(filename);
        IOException exception = mock(IOException.class);
        doThrow(exception).when(multipartFile).getInputStream();

        Upload upload = Upload.createFailedUpload(processedFile, path, exception);
        Upload result = uploader.upload(processedFile);

        assertEquals(upload, result);
    }
}
