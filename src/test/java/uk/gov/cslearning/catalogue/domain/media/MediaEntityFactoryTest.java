package uk.gov.cslearning.catalogue.domain.media;

import org.junit.Test;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.exception.UnknownFileTypeException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MediaEntityFactoryTest {

    private final MediaEntityFactory mediaFactory = new MediaEntityFactory();

    @Test
    public void shouldReturnMediaFromFileUpload() {
        long size = 10;
        String name = "file-name";
        String extension = "doc";
        String fileContainer = "file-container";
        FileUpload fileUpload = mock(FileUpload.class);
        when(fileUpload.getName()).thenReturn(name);
        when(fileUpload.getContainer()).thenReturn(fileContainer);
        when(fileUpload.getExtension()).thenReturn(extension);
        when(fileUpload.getSize()).thenReturn(size);

        Media media = mediaFactory.create(fileUpload);

        assertTrue(media.getDateAdded().isBefore(LocalDateTime.now()));
        assertTrue(media.getDateAdded().isAfter(LocalDateTime.now().minus(5, ChronoUnit.SECONDS)));
        assertEquals(name, media.getName());
        assertEquals(extension, media.getExtension());
        assertEquals("10 KB", media.formatFileSize());
        assertEquals(fileContainer, media.getContainer());

        assertEquals(String.join("/", fileContainer, media.getUid()), media.getPath());
        assertEquals(22, media.getId().length());
        assertEquals(media.getId(), media.getUid());
    }


    @Test
    public void shouldThrowUnknownFileTypeExceptionIfExtensionNotRecognised() {
        String extension = "xxx";
        FileUpload fileUpload = mock(FileUpload.class);
        when(fileUpload.getExtension()).thenReturn(extension);

        try {
            mediaFactory.create(fileUpload);
            fail("Expected UnknownFileTypeException");
        } catch (UnknownFileTypeException e) {
            assertEquals("Uploaded file has an unknown extension: xxx", e.getMessage());
        }
    }
}