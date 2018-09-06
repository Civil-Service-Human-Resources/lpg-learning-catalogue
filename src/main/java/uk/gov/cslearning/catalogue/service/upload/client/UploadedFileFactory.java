package uk.gov.cslearning.catalogue.service.upload.client;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.UploadStatus;
import uk.gov.cslearning.catalogue.dto.UploadedFile;

@Component
public class UploadedFileFactory {

    public UploadedFile successulUploadedFile(String filePath, long fileSize /* in bytes */) {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setSizeKB(fileSize/1024);
        uploadedFile.setPath(filePath);
        uploadedFile.setStatus(UploadStatus.SUCCESS);

        return uploadedFile;
    }

    public UploadedFile failedUploadedFile(String filePath, long fileSize /* in bytes */, Throwable error) {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setSizeKB(fileSize/1024);
        uploadedFile.setPath(filePath);
        uploadedFile.setStatus(UploadStatus.FAIL);
        uploadedFile.setError(error);

        return uploadedFile;
    }
}
