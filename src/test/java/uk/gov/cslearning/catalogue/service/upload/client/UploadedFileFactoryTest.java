package uk.gov.cslearning.catalogue.service.upload.client;

import org.junit.Test;
import uk.gov.cslearning.catalogue.dto.UploadStatus;
import uk.gov.cslearning.catalogue.dto.UploadedFile;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class UploadedFileFactoryTest {

    private UploadedFileFactory uploadedFileFactory = new UploadedFileFactory();


    @Test
    public void successfulUploadedFileReturnsUploadedFile() {
        String filePath = "test-file-path";
        long fileSize = 99;

        UploadedFile uploadedFile = uploadedFileFactory.successulUploadedFile(filePath, fileSize);

        assertEquals(UploadStatus.SUCCESS, uploadedFile.getStatus());
        assertEquals(filePath, uploadedFile.getPath());
        assertEquals(fileSize, uploadedFile.getSize());
    }

    @Test
    public void failedUploadedFileReturnsUploadedFile() {
        String filePath = "test-file-path";
        long fileSize = 99;
        Throwable throwable = mock(Throwable.class);

        UploadedFile uploadedFile = uploadedFileFactory.failedUploadedFile(filePath, fileSize, throwable);

        assertEquals(UploadStatus.FAIL, uploadedFile.getStatus());
        assertEquals(filePath, uploadedFile.getPath());
        assertEquals(fileSize, uploadedFile.getSize());
        assertEquals(throwable, uploadedFile.getError());
    }

}