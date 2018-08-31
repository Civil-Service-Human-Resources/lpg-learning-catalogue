package uk.gov.cslearning.catalogue.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Map;

public class ProcessedFile {
    private FileUpload fileUpload;
    private Map<String, String> metadata;

    public FileUpload getFileUpload() {
        return fileUpload;
    }

    public void setFileUpload(FileUpload fileUpload) {
        this.fileUpload = fileUpload;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("fileUpload", fileUpload)
                .append("metadata", metadata)
                .toString();
    }
}
