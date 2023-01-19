package uk.gov.cslearning.catalogue.dto.upload;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import uk.gov.cslearning.catalogue.dto.UploadStatus;

import java.time.Clock;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@ToString
public class UploadedFile {
    private final long sizeKB;
    private final String path;
    private final UploadStatus status;
    private final LocalDateTime timestamp = LocalDateTime.now(Clock.systemUTC());

}
