package uk.gov.cslearning.catalogue.domain;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.Upload;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
public class MediaFactory {

    public Media create(Upload upload) {

        Media media = new Media();
        media.setId(upload.getProcessedFile().getFileUpload().getId());
        media.setContainer(upload.getProcessedFile().getFileUpload().getContainer());
        media.setDateAdded(LocalDateTime.now(Clock.systemUTC()));
        media.setExtension(upload.getProcessedFile().getFileUpload().getExtension());
        media.setName(upload.getProcessedFile().getFileUpload().getName());
        media.setPath(upload.getPath());
        media.setFileSizeKB(upload.getSizeKB());
        media.setMetadata(upload.getProcessedFile().getMetadata());

        return media;

    }
}
