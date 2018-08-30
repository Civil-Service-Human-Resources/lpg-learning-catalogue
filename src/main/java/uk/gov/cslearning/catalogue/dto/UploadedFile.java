package uk.gov.cslearning.catalogue.dto;

public class UploadedFile {
    private long size;
    private String path;
    private UploadStatus status;
    private Throwable error;

    public UploadedFile() {
    }

    public UploadedFile(UploadedFile uploadedFile) {
        this.size = uploadedFile.getSize();
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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
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
}
