package uk.gov.cslearning.catalogue.dto;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class UploadTest {
    @Test
    public void sizeReturnsSizeOfAllUploadedFiles() {
        UploadedFile file1 = new UploadedFile();
        file1.setSizeKB(1);
        UploadedFile file2 = new UploadedFile();
        file2.setSizeKB(10);
        UploadedFile file3 = new UploadedFile();
        file3.setSizeKB(100);

        Upload upload = new Upload(mock(ProcessedFile.class));
        upload.setUploadedFiles(Arrays.asList(file1, file2, file3));

        assertEquals(111, upload.getSizeKB());
    }

    @Test
    public void addToUploadedFilesAddsNewUploadedFile() {
        UploadedFile uploadedFile = new UploadedFile();

        Upload upload = new Upload(mock(ProcessedFile.class));

        assertEquals(0, upload.getUploadedFiles().size());

        upload.addToUploadedFiles(uploadedFile);

        assertEquals(1, upload.getUploadedFiles().size());

        assertNotSame(uploadedFile, upload.getUploadedFiles().get(0)); // prevent reference escape
    }

    @Test
    public void setUploadedFilesClonesList() {
        List<UploadedFile> uploadedFiles = Collections.singletonList(new UploadedFile());

        Upload upload = new Upload(mock(ProcessedFile.class));

        upload.setUploadedFiles(uploadedFiles);

        assertNotSame(uploadedFiles, upload.getUploadedFiles());
    }

    @Test
    public void toStringShouldIncludeAllFields() {
        Upload upload = new Upload(mock(ProcessedFile.class));
        upload.setUploadedFiles(Collections.singletonList(new UploadedFile()));
        upload.setStatus(UploadStatus.FAIL);
        upload.setPath("test-path");
        upload.setError(mock(Throwable.class));

        String pattern = "uk\\.gov\\.cslearning\\.catalogue\\.dto\\.Upload@\\w+\\[processedFile=Mock for " +
                "ProcessedFile, hashCode: \\d+,uploadedFiles=\\[[^]]+]],status=FAIL,path=test-path,error=Mock" +
                " for Throwable, hashCode: \\d+," +
                "timestamp=\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d]";

        assertTrue(Pattern.matches(pattern, upload.toString()));
    }
}