package uk.gov.cslearning.catalogue.dto.upload;

import uk.gov.cslearning.catalogue.dto.UploadStatus;

import java.util.List;

public class FailedUpload extends Upload {

    private List<Throwable> errors;

    public FailedUpload(ProcessedFileUpload processedFileUpload, List<Throwable> errors, String path) {
        super(processedFileUpload, UploadStatus.FAIL, path);
        this.errors = errors;
    }
}
