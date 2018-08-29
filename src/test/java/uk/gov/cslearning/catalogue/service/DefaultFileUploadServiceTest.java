package uk.gov.cslearning.catalogue.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.service.upload.DefaultFileUploadService;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClientFactory;
import uk.gov.cslearning.catalogue.service.upload.processor.FileProcessor;
import uk.gov.cslearning.catalogue.service.upload.processor.FileProcessorFactory;
import uk.gov.cslearning.catalogue.service.upload.uploader.Uploader;
import uk.gov.cslearning.catalogue.service.upload.uploader.UploaderFactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultFileUploadServiceTest {

    @Mock
    private FileProcessorFactory fileProcessorFactory;

    @Mock
    private UploadClientFactory uploadClientFactory;

    @Mock
    private UploaderFactory uploaderFactory;

    @InjectMocks
    private DefaultFileUploadService fileUploadService;

    @Test
    public void shouldReturnUpload() {
        FileUpload fileUpload = mock(FileUpload.class);
        FileProcessor fileProcessor = mock(FileProcessor.class);
        when(fileProcessorFactory.create(fileUpload)).thenReturn(fileProcessor);

        ProcessedFile processedFile = mock(ProcessedFile.class);
        when(fileProcessor.process(fileUpload)).thenReturn(processedFile);

        UploadClient uploadClient = mock(UploadClient.class);
        when(uploadClientFactory.create(processedFile)).thenReturn(uploadClient);

        Uploader uploader = mock(Uploader.class);
        when(uploaderFactory.create(processedFile)).thenReturn(uploader);

        Upload upload = mock(Upload.class);
        when(uploader.upload(processedFile, uploadClient)).thenReturn(upload);

        assertEquals(upload, fileUploadService.upload(fileUpload));
    }
}