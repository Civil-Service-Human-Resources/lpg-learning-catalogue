package uk.gov.cslearning.catalogue.domain.media;

import java.time.LocalDateTime;

public interface Media {
    long getFileSize();

    String getContainer();

    LocalDateTime getDateAdded();

    String getExtension();

    String getId();

    String getName();

    String getPath();

    String getUid();

    String formatFileSize();
}
