package uk.gov.cslearning.catalogue.domain.media;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import uk.gov.cslearning.catalogue.domain.media.factory.MediaEntityFactory;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.exception.UnknownFileTypeException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MediaEntityFactoryTest {

    private final MediaEntityFactory mediaEntityFactory = new MediaEntityFactory(ImmutableMap.of(
            "doc", (upload) -> new Document()
    ));

    @Test
    public void shouldCallFunctionWithValidExtension() {
        FileUpload fileUpload = mock(FileUpload.class);
        String extension = "doc";
        when(fileUpload.getExtension()).thenReturn(extension);

        ProcessedFile processedFile = mock(ProcessedFile.class);
        when(processedFile.getFileUpload()).thenReturn(fileUpload);

        Upload upload = mock(Upload.class);
        when(upload.getProcessedFile()).thenReturn(processedFile);

        assertTrue(mediaEntityFactory.create(upload) instanceof Document);
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
            assertTrue(e.getMessage().contains("Uploaded file has an unknown extension: xxx"));
        }
    }
}