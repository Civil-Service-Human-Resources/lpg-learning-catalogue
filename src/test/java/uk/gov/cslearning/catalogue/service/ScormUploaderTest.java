package uk.gov.cslearning.catalogue.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.cslearning.catalogue.dto.*;
import uk.gov.cslearning.catalogue.service.upload.ZipInputStreamFactory;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;
import uk.gov.cslearning.catalogue.service.upload.uploader.ScormUploader;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ScormUploaderTest {

    @InjectMocks
    private ScormUploader uploader;

    @Mock
    private ZipInputStreamFactory zipInputStreamFactory;

    @Test
    public void uploadUnzipsFileAndUploadsEntries() throws Exception {

        UploadClient uploadClient = mock(UploadClient.class);

        ProcessedFile processedFile = mock(ProcessedFile.class);
        FileUpload fileUpload = mock(FileUpload.class);

        when(processedFile.getFileUpload()).thenReturn(fileUpload);

        MultipartFile multipartFile = mock(MultipartFile.class);
        InputStream fileInputStream = mock(InputStream.class);

        when(fileUpload.getFile()).thenReturn(multipartFile);
        when(multipartFile.getInputStream()).thenReturn(fileInputStream);

        ZipInputStream zipInputStream = mock(ZipInputStream.class);

        when(zipInputStreamFactory.create(fileInputStream)).thenReturn(zipInputStream);

        ZipEntry zipEntry = mock(ZipEntry.class);

        when(zipInputStream.getNextEntry())
                .thenReturn(zipEntry)
                .thenReturn(null);

        String containerName = "container";
        when(fileUpload.getContainer()).thenReturn(containerName);
        String fileUploadId = "file-upload-id";
        when(fileUpload.getId()).thenReturn(fileUploadId);
        String zipEntryName = "zip-entry-name";
        when(zipEntry.getName()).thenReturn(zipEntryName);

        int size = 1024;
        UploadedFile uploadedFile = mock(UploadedFile.class);
        when(uploadedFile.getSize()).thenReturn(size);
        when(uploadClient.upload(zipInputStream, String.join("/", containerName, fileUploadId, zipEntryName), 1024)).thenReturn(uploadedFile);

        when(zipInputStream.read(any()))
                .thenReturn(size)
                .thenReturn(0);

        Upload upload = uploader.upload(processedFile, uploadClient);

        assertEquals(UploadStatus.OK, upload.getStatus());
        assertEquals(size, upload.getUploadedFiles().get(0).getSize());

        verify(zipInputStream).closeEntry();
        verify(zipInputStream).close();
    }

    @Test
    public void uploadAddsErrorToUploadOnFailure() throws Exception {

        ProcessedFile processedFile = mock(ProcessedFile.class);
        FileUpload fileUpload = mock(FileUpload.class);

        when(processedFile.getFileUpload()).thenReturn(fileUpload);

        MultipartFile multipartFile = mock(MultipartFile.class);
        InputStream fileInputStream = mock(InputStream.class);

        when(fileUpload.getFile()).thenReturn(multipartFile);

        IOException exception = mock(IOException.class);

        doThrow(exception).when(multipartFile).getInputStream();

        Upload upload = uploader.upload(processedFile, mock(UploadClient.class));

        assertEquals(UploadStatus.FAIL, upload.getStatus());
        assertTrue(upload.getError().isPresent());
        assertEquals(exception, upload.getError().get());
    }
}