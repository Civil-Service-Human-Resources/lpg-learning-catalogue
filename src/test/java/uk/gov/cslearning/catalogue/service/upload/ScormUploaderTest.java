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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ScormUploaderTest {

    @Mock
    private InputStreamFactory inputStreamFactory;

    @Mock
    private UploadFactory uploadFactory;

    @Mock
    private FileFactory fileFactory;

    @Mock
    private Map<String, String> fileSubstitions;

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

        int size = 1024;
        UploadedFile uploadedFile = mock(UploadedFile.class);
        String destinationDirectory = String.join("/", containerName, fileUploadId);

        when(uploadClient.upload(zipInputStream, String.join("/", destinationDirectory, zipEntryName), 1024)).thenReturn(uploadedFile);

        when(zipInputStream.read(any()))
                .thenReturn(size)
                .thenReturn(0);

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


    @Test
    public void shouldSubstituteFileIfExistsInMap() throws IOException, URISyntaxException {
        UploadClient uploadClient = mock(UploadClient.class);

        ProcessedFile processedFile = mock(ProcessedFile.class);
        FileUpload fileUpload = mock(FileUpload.class);

        when(processedFile.getFileUpload()).thenReturn(fileUpload);

        MultipartFile multipartFile = mock(MultipartFile.class);
        InputStream inputStream = mock(InputStream.class);

        when(fileUpload.getFile()).thenReturn(multipartFile);
        when(multipartFile.getInputStream()).thenReturn(inputStream);

        ZipInputStream zipInputStream = mock(ZipInputStream.class);

        when(inputStreamFactory.createZipInputStream(inputStream)).thenReturn(zipInputStream);

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
        String destinationDirectory = String.join("/", containerName, fileUploadId);

        when(fileSubstitions.containsKey(zipEntryName))
                .thenReturn(true)
                .thenReturn(false);
        String filePath = "file-path";
        when(fileSubstitions.get(zipEntryName)).thenReturn(filePath);
        File file = mock(File.class);
        when(fileFactory.get(filePath)).thenReturn(file);
        when(file.length()).thenReturn(1024L);
        FileInputStream fileInputStream = mock(FileInputStream.class);
        when(inputStreamFactory.createFileInputStream(file)).thenReturn(fileInputStream);

        when(uploadClient.upload(fileInputStream, String.join("/", destinationDirectory, zipEntryName), 1024)).thenReturn(uploadedFile);

        Upload upload = mock(Upload.class);
        when(uploadFactory.createUpload(eq(processedFile), eq(Collections.singletonList(uploadedFile)), eq(destinationDirectory))).thenReturn(upload);

        Upload result = uploader.upload(processedFile, uploadClient);

        assertEquals(upload, result);

        verify(zipInputStream).closeEntry();
        verify(zipInputStream).close();
    }
}