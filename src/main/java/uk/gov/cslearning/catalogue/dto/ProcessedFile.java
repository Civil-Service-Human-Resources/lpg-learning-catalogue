package uk.gov.cslearning.catalogue.dto;

import com.google.common.collect.ImmutableMap;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class ProcessedFile {
    private final FileUpload fileUpload;
    private Map<String, String> metadata = new HashMap<>();
    private final LocalDateTime timestamp = LocalDateTime.now(Clock.systemUTC());

    public static ProcessedFile createWithMetadata(FileUpload fileUpload, Map<String, String> metadata) {
        ProcessedFile processedFile = new ProcessedFile(fileUpload);
        processedFile.setMetadata(metadata);
        return processedFile;
    }

    public Map<String, Object> getMetadata() {
        return ImmutableMap.copyOf(metadata);
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = ImmutableMap.copyOf(metadata);
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
