package uk.gov.cslearning.catalogue.domain.media;

import org.elasticsearch.common.UUIDs;
import org.junit.Test;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.exception.UnknownFileTypeException;

import java.time.Clock;
import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MediaEntityFactoryTest {

    private final MediaEntityFactory mediaEntityFactory = new MediaEntityFactory();

    @Test
    public void shouldReturnMediaFromFileUpload() {
        long size = 10;
        String name = "file-name";
        String extension = "doc";
        String fileContainer = "file-container";
        String fileUploadId = UUIDs.base64UUID();
        String path = String.join("/", fileContainer, fileUploadId);

        FileUpload fileUpload = mock(FileUpload.class);
        when(fileUpload.getId()).thenReturn(fileUploadId);
        when(fileUpload.getName()).thenReturn(name);
        when(fileUpload.getContainer()).thenReturn(fileContainer);
        when(fileUpload.getExtension()).thenReturn(extension);
        when(fileUpload.getSize()).thenReturn(size);

        ProcessedFile processedFile = mock(ProcessedFile.class);
        when(processedFile.getFileUpload()).thenReturn(fileUpload);

        Upload upload = mock(Upload.class);
        when(upload.getProcessedFile()).thenReturn(processedFile);
        when(upload.getSize()).thenReturn(size);
        when(upload.getPath()).thenReturn(path);


        MediaEntity media = mediaEntityFactory.create(upload);

        assertTrue(media.getDateAdded().isBefore(LocalDateTime.now(Clock.systemUTC()).plusSeconds(1)));
        assertTrue(media.getDateAdded().isAfter(LocalDateTime.now(Clock.systemUTC()).minusSeconds(5)));
        assertEquals(name, media.getName());
        assertEquals(extension, media.getExtension());
        assertEquals("10 KB", media.formatFileSize());
        assertEquals(fileContainer, media.getContainer());

        assertEquals(path, media.getPath());
        assertEquals(fileUploadId, media.getId());
        assertEquals(media.getId(), media.getUid());
    }


    @Test
    public void shouldThrowUnknownFileTypeExceptionIfExtensionNotRecognised() {
        String extension = "xxx";
        FileUpload fileUpload = mock(FileUpload.class);
        when(fileUpload.getExtension()).thenReturn(extension);

        ProcessedFile processedFile = mock(ProcessedFile.class);
        when(processedFile.getFileUpload()).thenReturn(fileUpload);

        Upload upload = mock(Upload.class);
        when(upload.getProcessedFile()).thenReturn(processedFile);

        try {
            mediaEntityFactory.create(upload);
            fail("Expected UnknownFileTypeException");
        } catch (UnknownFileTypeException e) {
            assertEquals("Uploaded file has an unknown extension: xxx", e.getMessage());
        }
    }
}