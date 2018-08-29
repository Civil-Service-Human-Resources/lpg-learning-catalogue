package uk.gov.cslearning.catalogue.dto;

public class UploadedFile {
    private int size;

    public UploadedFile() {
    }

    public UploadedFile(UploadedFile uploadedFile) {
        this.size = uploadedFile.getSize();
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
