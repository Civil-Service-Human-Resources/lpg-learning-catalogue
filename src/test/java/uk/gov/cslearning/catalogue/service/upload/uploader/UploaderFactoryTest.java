package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.exception.UnknownFileTypeException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UploaderFactoryTest {

    @Mock
    private DefaultUploader defaultUploader;

    @Mock
    private ScormUploader scormUploader;

    @InjectMocks
    private UploaderFactory uploaderFactory;

    @Test
    public void shouldReturnDefaultUploaderWithDocExtension() {
        String extension = "doc";
        FileUpload fileUpload = mock(FileUpload.class);
        ProcessedFile processedFile = mock(ProcessedFile.class);

        when(fileUpload.getExtension()).thenReturn(extension);
        when(processedFile.getFileUpload()).thenReturn(fileUpload);

        Uploader uploader = uploaderFactory.create(processedFile);

        assertTrue(uploader instanceof DefaultUploader);
    }

    @Test
    public void shouldReturnScormUploaderWithZipExtension() {
        String extension = "zip";
        FileUpload fileUpload = mock(FileUpload.class);
        ProcessedFile processedFile = mock(ProcessedFile.class);

        when(fileUpload.getExtension()).thenReturn(extension);
        when(processedFile.getFileUpload()).thenReturn(fileUpload);

        Uploader uploader = uploaderFactory.create(processedFile);

        assertTrue(uploader instanceof ScormUploader);
    }

    @Test
    public void shouldReturnThrowUnknownFileTypeExceptionWithUnmatchedExtension() {
        String extension = "xxx";
        FileUpload fileUpload = mock(FileUpload.class);
        ProcessedFile processedFile = mock(ProcessedFile.class);

        when(fileUpload.getExtension()).thenReturn(extension);
        when(processedFile.getFileUpload()).thenReturn(fileUpload);

        try {
            Uploader uploader = uploaderFactory.create(processedFile);
            fail("Expected UknownFileTypeException");
        } catch (UnknownFileTypeException e) {
            assertEquals("Uploaded file has an unknown extension: xxx", e.getMessage());
        }
    }


}