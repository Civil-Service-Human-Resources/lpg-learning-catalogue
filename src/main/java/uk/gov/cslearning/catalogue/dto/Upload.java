package uk.gov.cslearning.catalogue.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Upload {
    private ProcessedFile processedFile;
    private List<UploadedFile> uploadedFiles = new ArrayList<>();
    private UploadStatus status;
    private String path;
    private Throwable error;

    public ProcessedFile getProcessedFile() {
        return processedFile;
    }

    public void setProcessedFile(ProcessedFile processedFile) {
        this.processedFile = processedFile;
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

    public long getSize() {
        return uploadedFiles.stream().mapToLong(UploadedFile::getSize).sum();
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
}
