package uk.gov.cslearning.catalogue.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.Clock;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UploadedFile {
    private long sizeKB;
    private String path;
    private UploadStatus status;
    private Throwable error;
    private final LocalDateTime timestamp = LocalDateTime.now(Clock.systemUTC());

    public UploadedFile(UploadedFile uploadedFile) {
        this.sizeKB = uploadedFile.getSizeKB();
        this.path = uploadedFile.getPath();
        this.status = uploadedFile.getStatus();
        this.error = uploadedFile.getError();
    }

    public static UploadedFile createSuccessulUploadedFile(String filePath, long fileSizeBytes) {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setSizeKB(fileSizeBytes/1024);
        uploadedFile.setPath(filePath);
        uploadedFile.setStatus(UploadStatus.SUCCESS);

        return uploadedFile;
    }

    public static UploadedFile createFailedUploadedFile(String filePath, long fileSizeBytes, Throwable error) {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setSizeKB(fileSizeBytes/1024);
        uploadedFile.setPath(filePath);
        uploadedFile.setStatus(UploadStatus.FAIL);
        uploadedFile.setError(error);

        return uploadedFile;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("sizeKB", sizeKB)
                .append("path", path)
                .append("status", status)
                .append("error", error)
                .append("timestamp", timestamp)
                .toString();
    }
}
