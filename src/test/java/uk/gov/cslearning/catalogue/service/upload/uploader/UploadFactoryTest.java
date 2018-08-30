package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.junit.Test;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.dto.UploadStatus;
import uk.gov.cslearning.catalogue.dto.UploadedFile;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class UploadFactoryTest {
    private final UploadFactory uploadFactory = new UploadFactory();

    @Test
    public void successfulUploadReturnsUpload() {
        ProcessedFile processedFile = mock(ProcessedFile.class);
        UploadedFile uploadedFile = mock(UploadedFile.class);
        String path = "test-path";

        Upload upload = uploadFactory.successfulUpload(processedFile, Collections.singletonList(uploadedFile), path);

        assertEquals(processedFile, upload.getProcessedFile());
        assertEquals(path, upload.getPath());
        assertEquals(uploadedFile, upload.getUploadedFiles().get(0));
        assertEquals(UploadStatus.SUCCESS, upload.getStatus());
    }

    @Test
    public void failedUploadReturnsUpload() {
        ProcessedFile processedFile = mock(ProcessedFile.class);
        Throwable throwable = mock(Throwable.class);
        String path = "test-path";

        Upload upload = uploadFactory.failedUpload(processedFile, path, throwable);

        assertEquals(processedFile, upload.getProcessedFile());
        assertEquals(path, upload.getPath());
        assertEquals(UploadStatus.FAIL, upload.getStatus());
        assertTrue(upload.getError().isPresent());
        assertEquals(throwable, upload.getError().get());
    }
}