package uk.gov.cslearning.catalogue.dto;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public interface FileUpload {
    String getId();

    String getContainer();

    String getExtension();

    MultipartFile getFile();

    String getName();

    long getSizeKB();

    LocalDateTime getTimestamp();
}
