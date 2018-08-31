package uk.gov.cslearning.catalogue.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;

public class ProcessedFile {
    private final FileUpload fileUpload;
    private Map<String, String> metadata;
    private final LocalDateTime timestamp = LocalDateTime.now(Clock.systemUTC());

    public ProcessedFile(FileUpload fileUpload) {
        this.fileUpload = fileUpload;
    }

    public FileUpload getFileUpload() {
        return fileUpload;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("fileUpload", fileUpload)
                .append("metadata", metadata)
                .append("timestamp", timestamp)
                .toString();
    }
}
