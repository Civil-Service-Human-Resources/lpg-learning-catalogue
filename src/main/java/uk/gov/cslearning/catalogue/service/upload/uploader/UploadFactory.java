package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.dto.UploadStatus;
import uk.gov.cslearning.catalogue.dto.UploadedFile;

import java.util.List;

@Component
public class UploadFactory {
    public Upload successfulUpload(ProcessedFile processedFile, List<UploadedFile> uploadedFiles, String path) {
        Upload upload = new Upload();
        upload.setStatus(UploadStatus.OK);
        upload.setUploadedFiles(uploadedFiles);
        upload.setProcessedFile(processedFile);
        upload.setPath(path);

        return upload;
    }

    public Upload failedUpload(ProcessedFile processedFile, String path, Throwable e) {
        Upload upload = new Upload();
        upload.setStatus(UploadStatus.FAIL);
        upload.setProcessedFile(processedFile);
        upload.setPath(path);
        upload.setError(e);

        return upload;
    }
}
