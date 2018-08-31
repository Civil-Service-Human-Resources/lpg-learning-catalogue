package uk.gov.cslearning.catalogue.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class UploadedFile {
    private long sizeKB;
    private String path;
    private UploadStatus status;
    private Throwable error;

    public UploadedFile() {
    }

    public UploadedFile(UploadedFile uploadedFile) {
        this.sizeKB = uploadedFile.getSizeKB();
        this.path = uploadedFile.getPath();
        this.status = uploadedFile.getStatus();
        this.error = uploadedFile.getError();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSizeKB() {
        return sizeKB;
    }

    public void setSizeKB(long sizeKB) {
        this.sizeKB = sizeKB;
    }

    public UploadStatus getStatus() {
        return status;
    }

    public void setStatus(UploadStatus status) {
        this.status = status;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("sizeKB", sizeKB)
                .append("path", path)
                .append("status", status)
                .append("error", error)
                .toString();
    }
}
