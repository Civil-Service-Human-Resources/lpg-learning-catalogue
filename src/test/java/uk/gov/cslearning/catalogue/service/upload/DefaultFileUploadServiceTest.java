package uk.gov.cslearning.catalogue.service.upload;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.exception.FileUploadException;
import uk.gov.cslearning.catalogue.exception.UnknownFileTypeException;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;
import uk.gov.cslearning.catalogue.service.upload.processor.FileProcessor;
import uk.gov.cslearning.catalogue.service.upload.processor.FileProcessorFactory;
import uk.gov.cslearning.catalogue.service.upload.processor.ImageProcessorFactory;
import uk.gov.cslearning.catalogue.service.upload.uploader.Uploader;
import uk.gov.cslearning.catalogue.service.upload.uploader.UploaderFactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class DefaultFileUploadServiceTest {

    @Mock
    private FileProcessorFactory fileProcessorFactory;

    @Mock
    private UploadClientFactory uploadClientFactory;

    @Mock
    private UploaderFactory uploaderFactory;

    @Mock
    private ImageProcessorFactory imageProcessorFactory;

    @InjectMocks
    private DefaultFileUploadService fileUploadService;

    @Test
    public void shouldReturnUpload() {
        FileUpload fileUpload = mock(FileUpload.class);
        FileProcessor fileProcessor = mock(FileProcessor.class);
        when(fileProcessorFactory.create(fileUpload)).thenReturn(fileProcessor);

        Upload upload = verifyUpload(fileUpload, fileProcessor);

        assertEquals(upload, fileUploadService.upload(fileUpload));
    }

    @Test
    public void shouldReturnUploadForImages() throws IOException {
        FileUpload fileUpload = mock(FileUpload.class);
        FileProcessor fileProcessor = mock(FileProcessor.class);
        when(imageProcessorFactory.create(fileUpload)).thenReturn(fileProcessor);

        Upload upload = verifyUpload(fileUpload, fileProcessor);

        assertEquals(upload, fileUploadService.uploadImageForSkills(fileUpload));
    }

    @Test(expected = UnknownFileTypeException.class)
    public void shouldThrowExceptionForUnknownImageType() throws IOException {
        FileUpload fileUpload = getFileUpload();

        doThrow(
                new UnknownFileTypeException(
                        String.format(
                                "Uploaded file has an unknown extension: %s",
                                fileUpload.getExtension())))
                .when(imageProcessorFactory)
                .validateProcessedFile(fileUpload);
        fileUploadService.uploadImageForSkills(fileUpload);
    }

    @Test(expected = FileUploadException.class)
    public void shouldThrowExceptionIfExceedsFileSizeLimit() throws IOException {
        FileUpload fileUpload = getFileUpload();

        doThrow(
                new FileUploadException("Uploaded file exceeds maximum allowed size of 5MB"))
                .when(imageProcessorFactory)
                .validateProcessedFile(fileUpload);

        fileUploadService.uploadImageForSkills(fileUpload);
    }

    @Test(expected = FileUploadException.class)
    public void shouldThrowExceptionIfExceedsFileDimensionsLimit() throws IOException {
        FileUpload fileUpload = getFileUpload();

        doThrow(
                new FileUploadException(
                        "Uploaded file does not meet the required dimensions." +
                        " 960p width X 640p height"))
                .when(imageProcessorFactory)
                .validateProcessedFile(fileUpload);

        fileUploadService.uploadImageForSkills(fileUpload);
    }

    private FileUpload getFileUpload() {
        byte[] array = {};
        MockMultipartFile mockMultipartFile = new MockMultipartFile("test_file.pdf", array);

        return FileUpload.createFromMetadata(mockMultipartFile, "", "test_file.pdf");
    }

    private Upload verifyUpload(FileUpload fileUpload, FileProcessor fileProcessor) {
        ProcessedFile processedFile = mock(ProcessedFile.class);
        when(fileProcessor.process(fileUpload)).thenReturn(processedFile);

        UploadClient uploadClient = mock(UploadClient.class);
        when(uploadClientFactory.create(processedFile)).thenReturn(uploadClient);

        Uploader uploader = mock(Uploader.class);
        when(uploaderFactory.create(processedFile)).thenReturn(uploader);

        Upload upload = mock(Upload.class);
        when(uploader.upload(processedFile, uploadClient)).thenReturn(upload);

        return upload;
    }
}
