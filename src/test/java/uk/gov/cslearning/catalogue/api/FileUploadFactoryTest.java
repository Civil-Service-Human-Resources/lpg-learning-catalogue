package uk.gov.cslearning.catalogue.api;

import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.service.upload.FileUploadFactory;

import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileUploadFactoryTest {

    private final FileUploadFactory fileUploadFactory = new FileUploadFactory();

    @Test
    public void createReturnsFileUpload() {
        String container = "test-container";
        String originalFilename = "original-filename.xls";
        String filename = "custom-filename.doc";
        long fileSizeBytes = 10240;

        MultipartFile file = mock(MultipartFile.class);

        when(file.getSize()).thenReturn(fileSizeBytes);
        when(file.getOriginalFilename()).thenReturn(originalFilename);

        FileUpload result = fileUploadFactory.create(file, container, filename);

        assertEquals(container, result.getContainer());
        assertEquals(filename, result.getName());
        assertEquals(file, result.getFile());
        assertEquals("xls", result.getExtension());
        assertEquals(10, result.getSizeKB());
        assertEquals(22, result.getId().length());
    }

    @Test
    public void createReturnsFileUploadWithOriginalFilename() {
        String container = "test-container";
        String originalFilename = "original-filename.xls";
        long fileSizeBytes = 10240;

        MultipartFile file = mock(MultipartFile.class);

        when(file.getSize()).thenReturn(fileSizeBytes);
        when(file.getOriginalFilename()).thenReturn(originalFilename);

        FileUpload result = fileUploadFactory.create(file, container, null);

        assertEquals(container, result.getContainer());
        assertEquals(originalFilename, result.getName());
        assertEquals(file, result.getFile());
        assertEquals("xls", result.getExtension());
        assertEquals(10, result.getSizeKB());
    }

    @Test
    public void toStringContainsAllFields() {
        String container = "testcontainer";
        String name = "testname.xxx";
        MultipartFile file = mock(MultipartFile.class);
        long sizeKB = 99;
        when(file.getSize()).thenReturn(sizeKB * 1024);
        when(file.getOriginalFilename()).thenReturn(name);
        FileUpload fileUpload = fileUploadFactory.create(file, container, name);

        String pattern = "uk\\.gov\\.cslearning\\.catalogue\\.service\\.upload\\.FileUploadFactory\\$1@\\w+" +
                "\\[id=[^,]{22},container=testcontainer,file=Mock for MultipartFile, hashCode: \\d+,extension=xxx," +
                "name=testname\\.xxx,sizeKB=99," +
                "timestamp=\\d\\d\\d\\d\\-\\d\\d\\-\\d\\dT\\d\\d:\\d\\d:\\d\\d.\\d\\d\\d\\]";

        assertTrue(fileUpload.toString() ,Pattern.matches(pattern, fileUpload.toString()));
    }
}