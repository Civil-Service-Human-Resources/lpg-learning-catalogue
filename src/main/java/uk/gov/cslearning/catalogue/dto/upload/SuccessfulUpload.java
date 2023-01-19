package uk.gov.cslearning.catalogue.dto.upload;

import uk.gov.cslearning.catalogue.dto.UploadStatus;

import java.util.List;

public class SuccessfulUpload extends Upload {

    private final List<UploadedFile> files;

    public SuccessfulUpload(ProcessedFileUpload processedFileUpload, String path,
                            List<UploadedFile> uploadedFiles) {
        super(processedFileUpload, UploadStatus.SUCCESS, path);
        this.files = uploadedFiles;
    }

    public long getSizeKB() {
        return files.stream().mapToLong(UploadedFile::getSizeKB).sum();
    }
}
