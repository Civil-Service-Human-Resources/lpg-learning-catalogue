package uk.gov.cslearning.catalogue.dto.upload;

import uk.gov.cslearning.catalogue.dto.UploadStatus;

public class SuccessfulUploadedFile extends UploadedFile {

    public SuccessfulUploadedFile(long sizeKB, String path) {
        super(sizeKB, path, UploadStatus.SUCCESS);
    }
}
