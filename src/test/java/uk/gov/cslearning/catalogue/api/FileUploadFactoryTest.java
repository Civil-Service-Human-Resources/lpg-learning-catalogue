package uk.gov.cslearning.catalogue.api;

import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.service.FileUploadFactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileUploadFactoryTest {

    private final FileUploadFactory fileUploadFactory = new FileUploadFactory();

    @Test
    public void createReturnsFileUpload() {
        String container = "test-container";
        String fileName = "test.doc";
        long fileSizeBytes = 10240;

        MultipartFile file = mock(MultipartFile.class);

        when(file.getSize()).thenReturn(fileSizeBytes);
        when(file.getName()).thenReturn(fileName);

        FileUpload result = fileUploadFactory.create(file, container);

        assertEquals(container, result.getContainer());
        assertEquals(fileName, result.getName());
        assertEquals(file, result.getFile());
        assertEquals("doc", result.getExtension());
        assertEquals(10, result.getSize());
    }
}