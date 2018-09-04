package uk.gov.cslearning.catalogue.service.upload;

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
import uk.gov.cslearning.catalogue.service.upload.uploader.ScormUploader;
import uk.gov.cslearning.catalogue.service.upload.uploader.UploadFactory;
import uk.gov.cslearning.catalogue.service.upload.uploader.ZipEntryUploader;
import uk.gov.cslearning.catalogue.service.upload.uploader.ZipEntryUploaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ScormUploaderTest {

    @Mock
    private InputStreamFactory inputStreamFactory;

    @Mock
    private UploadFactory uploadFactory;

    @Mock
    private ZipEntryUploaderFactory zipEntryUploaderFactory;

    @InjectMocks
    private ScormUploader uploader;

    @Test
    public void uploadShouldUnzipFileAndUploadEntries() throws Exception {

        UploadClient uploadClient = mock(UploadClient.class);

        ProcessedFile processedFile = mock(ProcessedFile.class);
        FileUpload fileUpload = mock(FileUpload.class);

        when(processedFile.getFileUpload()).thenReturn(fileUpload);

        MultipartFile multipartFile = mock(MultipartFile.class);
        InputStream fileInputStream = mock(InputStream.class);

        when(fileUpload.getFile()).thenReturn(multipartFile);
        when(multipartFile.getInputStream()).thenReturn(fileInputStream);

        ZipInputStream zipInputStream = mock(ZipInputStream.class);

        when(inputStreamFactory.createZipInputStream(fileInputStream)).thenReturn(zipInputStream);

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

        UploadedFile uploadedFile = mock(UploadedFile.class);
        String destinationDirectory = String.join("/", containerName, fileUploadId);

        ZipEntryUploader zipEntryUploader = mock(ZipEntryUploader.class);
        when(zipEntryUploaderFactory.get(zipEntry)).thenReturn(zipEntryUploader);

        when(zipEntryUploader.upload(uploadClient, zipEntry, zipInputStream,
                String.join("/", destinationDirectory, zipEntryName))).thenReturn(Optional.of(uploadedFile));

        Upload upload = mock(Upload.class);
        when(uploadFactory.createUpload(eq(processedFile), eq(Collections.singletonList(uploadedFile)), eq(destinationDirectory))).thenReturn(upload);

        Upload result = uploader.upload(processedFile, uploadClient);

        assertEquals(upload, result);

        verify(zipInputStream).closeEntry();
        verify(zipInputStream).close();
    }

    @Test
    public void uploadAddsErrorToUploadOnFailure() throws Exception {
        String containerName = "container";
        String fileUploadId = "file-upload-id";
        String destinationDirectory = String.join("/", containerName, fileUploadId);

        FileUpload fileUpload = mock(FileUpload.class);
        when(fileUpload.getContainer()).thenReturn(containerName);
        when(fileUpload.getId()).thenReturn(fileUploadId);

        ProcessedFile processedFile = mock(ProcessedFile.class);
        when(processedFile.getFileUpload()).thenReturn(fileUpload);

        MultipartFile multipartFile = mock(MultipartFile.class);

        when(fileUpload.getFile()).thenReturn(multipartFile);

        IOException exception = mock(IOException.class);

        doThrow(exception).when(multipartFile).getInputStream();

        Upload upload = mock(Upload.class);
        when(uploadFactory.createFailedUpload(processedFile, destinationDirectory, exception)).thenReturn(upload);

        Upload result = uploader.upload(processedFile, mock(UploadClient.class));

        assertEquals(upload, result);
    }
}