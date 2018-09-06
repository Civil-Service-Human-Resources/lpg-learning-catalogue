package uk.gov.cslearning.catalogue.dto;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ProcessedFile {
    private final FileUpload fileUpload;
    private Map<String, Object> metadata = new HashMap<>();
    private final LocalDateTime timestamp = LocalDateTime.now(Clock.systemUTC());

    public ProcessedFile(FileUpload fileUpload) {
        this.fileUpload = fileUpload;
    }

    public FileUpload getFileUpload() {
        return fileUpload;
    }

    public Map<String, Object> getMetadata() {
        return ImmutableMap.copyOf(metadata);
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = ImmutableMap.copyOf(metadata);
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
