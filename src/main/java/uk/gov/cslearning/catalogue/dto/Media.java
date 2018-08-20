package uk.gov.cslearning.catalogue.dto;

import java.time.LocalDateTime;

public interface Media {
    String formatFileSize();

    String getContainer();

    LocalDateTime getDateAdded();

    String getExtension();

    long getId();

    String getName();

    String getPath();

    String getUid();
}
