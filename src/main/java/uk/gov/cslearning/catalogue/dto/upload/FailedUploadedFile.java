package uk.gov.cslearning.catalogue.dto.upload;

import uk.gov.cslearning.catalogue.dto.UploadStatus;

public class FailedUploadedFile extends UploadedFile {

    private final Throwable error;

    public FailedUploadedFile(long sizeKB, String path, Throwable error) {
        super(sizeKB, path, UploadStatus.FAIL);
        this.error = error;
    }

    public Throwable getError() {
        return error;
    }

}
