package uk.gov.cslearning.catalogue.domain.media;

import java.time.LocalDateTime;

public interface Media {

    String getId();
    String getName();
    long getFileSize();
    String formatFileSize();
    String getContainer();

    LocalDateTime getDateAdded();
    String getExtension();
    String getPath();
    String getUid();
}
