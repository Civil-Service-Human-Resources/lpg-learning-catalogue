package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.dto.UploadedFile;
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

    @Mock
    private UploadFactory uploadFactory;

    @InjectMocks
    private DefaultUploader uploader;

    @Test
    public void shouldUploadFileAndReturnUploadOnSuccessfulUpload() throws IOException {
        String containerName = "test-container";
        String id = "file-upload-id";
        long fileSize = 99;
        String contentType = "application/pdf";

        String path = String.join("/", containerName, id);
        FileUpload fileUpload = mock(FileUpload.class);
        ProcessedFile processedFile = mock(ProcessedFile.class);
        MultipartFile multipartFile = mock(MultipartFile.class);
        InputStream inputStream = mock(InputStream.class);

        when(processedFile.getFileUpload()).thenReturn(fileUpload);
        when(fileUpload.getContainer()).thenReturn(containerName);
        when(fileUpload.getId()).thenReturn(id);
        when(fileUpload.getFile()).thenReturn(multipartFile);
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(multipartFile.getSize()).thenReturn(fileSize);
        when(multipartFile.getContentType()).thenReturn(contentType);


        UploadClient uploadClient = mock(UploadClient.class);
        UploadedFile uploadedFile = mock(UploadedFile.class);
        when(uploadClient.upload(inputStream, path, fileSize, contentType)).thenReturn(uploadedFile);

        Upload upload = mock(Upload.class);

        when(uploadFactory.createUpload(processedFile, Collections.singletonList(uploadedFile), path)).thenReturn(upload);

        Upload result = uploader.upload(processedFile, uploadClient);

        assertEquals(upload, result);
    }

    @Test
    public void shouldAddErrorToUploadOnIOException() throws IOException {
        String containerName = "test-container";
        String id = "file-upload-id";

        String path = String.join("/", containerName, id);
        FileUpload fileUpload = mock(FileUpload.class);
        ProcessedFile processedFile = mock(ProcessedFile.class);
        MultipartFile multipartFile = mock(MultipartFile.class);

        when(processedFile.getFileUpload()).thenReturn(fileUpload);
        when(fileUpload.getContainer()).thenReturn(containerName);
        when(fileUpload.getId()).thenReturn(id);
        when(fileUpload.getFile()).thenReturn(multipartFile);
        IOException exception = mock(IOException.class);
        doThrow(exception).when(multipartFile).getInputStream();

        Upload upload = mock(Upload.class);
        when(uploadFactory.createFailedUpload(processedFile, path, exception)).thenReturn(upload);

        UploadClient uploadClient = mock(UploadClient.class);
        Upload result = uploader.upload(processedFile, uploadClient);

        assertEquals(upload, result);
    }
}