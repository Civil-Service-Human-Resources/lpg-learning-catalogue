package uk.gov.cslearning.catalogue.domain;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import uk.gov.cslearning.catalogue.dto.upload.FileUpload;
import uk.gov.cslearning.catalogue.dto.upload.ProcessedFileUpload;
import uk.gov.cslearning.catalogue.dto.upload.Upload;
import uk.gov.cslearning.catalogue.dto.upload.UploadedFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MediaFactoryTest {

    private final MediaFactory mediaFactory = new MediaFactory();

    @Test
    public void createReturnsMedia() {
        String id = "test-id";
        String container = "test-container";
        String extension = "zip";
        String name = "file-name";
        String path = "test-path";
        long sizeKB = 13;
        Map<String, String> metadata = ImmutableMap.of("key", "value");

        FileUpload fileUpload = mock(FileUpload.class);
        when(fileUpload.getId()).thenReturn(id);
        when(fileUpload.getContainer()).thenReturn(container);
        when(fileUpload.getExtension()).thenReturn(extension);
        when(fileUpload.getName()).thenReturn(name);

        ProcessedFileUpload processedFileUpload = new ProcessedFileUpload(fileUpload, Collections.emptyList(), metadata);
        UploadedFile uploadedFile = mock(UploadedFile.class);
        when(uploadedFile.getSizeKB()).thenReturn(sizeKB);
        List<UploadedFile> uploadedFileList = Collections.singletonList(uploadedFile);

        Upload upload = new Upload(processedFileUpload, uploadedFileList, path);

        Media media = mediaFactory.create(upload);

        assertEquals(id, media.getId());
        assertEquals(container, media.getContainer());
        assertEquals(extension, media.getExtension());
        assertEquals(name, media.getName());
        assertEquals(path, media.getPath());
        assertEquals(sizeKB, media.getFileSizeKB());
        assertEquals(metadata, media.getMetadata());
        assertEquals("13 kB", media.formatFileSize());
    }

}
