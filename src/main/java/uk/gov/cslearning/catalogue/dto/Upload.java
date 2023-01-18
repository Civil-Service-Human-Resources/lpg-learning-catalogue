package uk.gov.cslearning.catalogue.dto;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Upload {
    private final ProcessedFile processedFile;
    private List<UploadedFile> uploadedFiles = new ArrayList<>();
    private UploadStatus status;
    private String path;
    private Throwable error;
    private LocalDateTime timestamp = LocalDateTime.now(Clock.systemUTC());

    public static Upload createSuccessfulUpload(ProcessedFile processedFile, List<UploadedFile> uploadedFiles, String path) {
        Upload upload = new Upload(processedFile);
        upload.setStatus(UploadStatus.SUCCESS);
        upload.setUploadedFiles(uploadedFiles);
        upload.setPath(path);

        return upload;
    }

    public static Upload createFailedUpload(ProcessedFile processedFile, String path, Throwable e) {
        Upload upload = new Upload(processedFile);
        upload.setStatus(UploadStatus.FAIL);
        upload.setPath(path);
        upload.setError(e);

        return upload;
    }

    public Upload(ProcessedFile processedFile) {
        this.processedFile = processedFile;
    }

    public long getSizeKB() {
        return uploadedFiles.stream().mapToLong(UploadedFile::getSizeKB).sum();
    }

    public void addToUploadedFiles(UploadedFile uploadedFile) {
        uploadedFiles.add(new UploadedFile(uploadedFile));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("processedFile", processedFile)
                .append("uploadedFiles", uploadedFiles)
                .append("status", status)
                .append("path", path)
                .append("error", error)
                .append("timestamp", timestamp)
                .toString();
    }
}
