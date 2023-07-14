package uk.gov.cslearning.catalogue.dto.upload;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.annotation.Nullable;
import java.time.Clock;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@ToString
public class UploadedFile {
    private final long sizeKB;
    private final String path;
    private final UploadStatus status;
    private final @Nullable Throwable exception;
    private final LocalDateTime timestamp = LocalDateTime.now(Clock.systemUTC());

    public static UploadedFile createSuccessfulUploadedFile(long sizeKB, String path) {
        return new UploadedFile(sizeKB, path, UploadStatus.SUCCESS, null);
    }

    public static UploadedFile createFailedUploadedFile(long sizeKB, String path, Throwable e) {
        return new UploadedFile(sizeKB, path, UploadStatus.FAIL, e);
    }
}
