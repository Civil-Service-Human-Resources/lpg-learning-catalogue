package uk.gov.cslearning.catalogue.dto.upload;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
@RequiredArgsConstructor
public class Upload {
    private final ProcessedFileUpload processedFileUpload;
    private final List<UploadedFile> files;
    private final String path;
    private final LocalDateTime timestamp = LocalDateTime.now(Clock.systemUTC());
    public long getSizeKB() {
        return files.stream().mapToLong(UploadedFile::getSizeKB).sum();
    }

}
