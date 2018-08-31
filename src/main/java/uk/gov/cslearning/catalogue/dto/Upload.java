package uk.gov.cslearning.catalogue.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Upload {
    private final ProcessedFile processedFile;
    private List<UploadedFile> uploadedFiles = new ArrayList<>();
    private UploadStatus status;
    private String path;
    private Throwable error;

    public Upload(ProcessedFile processedFile) {
        this.processedFile = processedFile;
    }

    public ProcessedFile getProcessedFile() {
        return processedFile;
    }

    public List<UploadedFile> getUploadedFiles() {
        return uploadedFiles;
    }

    public void setUploadedFiles(List<UploadedFile> uploadedFiles) {
        this.uploadedFiles = new ArrayList<>(uploadedFiles);
    }

    public UploadStatus getStatus() {
        return status;
    }

    public void setStatus(UploadStatus status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSizeKB() {
        return uploadedFiles.stream().mapToLong(UploadedFile::getSizeKB).sum();
    }

    public void addToUploadedFiles(UploadedFile uploadedFile) {
        uploadedFiles.add(new UploadedFile(uploadedFile));
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public Optional<Throwable> getError() {
        return Optional.ofNullable(error);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("processedFile", processedFile)
                .append("uploadedFiles", uploadedFiles)
                .append("status", status)
                .append("path", path)
                .append("error", error)
                .toString();
    }
}
