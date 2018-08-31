package uk.gov.cslearning.catalogue.dto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UploadedFileTest {
    @Test
    public void shouldSetPropertiesFromUploadedFile() {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setSizeKB(3);

        UploadedFile newUploadedFile = new UploadedFile(uploadedFile);

        assertEquals(3, newUploadedFile.getSizeKB());
    }
}