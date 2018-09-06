package uk.gov.cslearning.catalogue.domain.media.factory;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import uk.gov.cslearning.catalogue.domain.media.Media;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.Upload;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateScormFunctionTest {

    private final CreateScormFunction function = new CreateScormFunction();

    @Test
    public void applyReturnsScorm() {
        String id = "test-id";
        String container = "test-container";
        String extension = "xxx";
        String name = "file-name";
        String path = "test-path";
        long sizeKB = 13;
        Map<String, Object> metadata = ImmutableMap.of("key", "value");

        FileUpload fileUpload = mock(FileUpload.class);
        when(fileUpload.getId()).thenReturn(id);
        when(fileUpload.getContainer()).thenReturn(container);
        when(fileUpload.getExtension()).thenReturn(extension);
        when(fileUpload.getName()).thenReturn(name);

        ProcessedFile processedFile = new ProcessedFile(fileUpload);
        processedFile.setMetadata(metadata);

        Upload upload = mock(Upload.class);
        when(upload.getProcessedFile()).thenReturn(processedFile);
        when(upload.getPath()).thenReturn(path);
        when(upload.getSizeKB()).thenReturn(sizeKB);

        Media media = function.apply(upload);

        assertEquals(id, media.getId());
        assertEquals(container, media.getContainer());
        assertEquals(extension, media.getExtension());
        assertEquals(name, media.getName());
        assertEquals(path, media.getPath());
        assertEquals(sizeKB, media.getFileSize());
        assertEquals(metadata, media.getMetadata());
        assertEquals("13 KB", media.formatFileSize());
        assertTrue(media.getDateAdded().isBefore(LocalDateTime.now(Clock.systemUTC()).plusSeconds(1)));
        assertTrue(media.getDateAdded().isAfter(LocalDateTime.now(Clock.systemUTC()).minusSeconds(5)));

    }
}