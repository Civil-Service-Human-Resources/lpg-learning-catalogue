package uk.gov.cslearning.catalogue.dto;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class UploadTest {
    @Test
    public void sizeReturnsSizeOfAllUploadedFiles() {
        UploadedFile file1 = new UploadedFile();
        file1.setSizeKB(1);
        UploadedFile file2 = new UploadedFile();
        file2.setSizeKB(10);
        UploadedFile file3 = new UploadedFile();
        file3.setSizeKB(100);

        Upload upload = new Upload();
        upload.setUploadedFiles(Arrays.asList(file1, file2, file3));


        assertEquals(111, upload.getSizeKB());


    }
}