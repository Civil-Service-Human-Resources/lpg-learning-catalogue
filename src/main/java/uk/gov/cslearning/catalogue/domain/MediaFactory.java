package uk.gov.cslearning.catalogue.domain;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.upload.ProcessedFileUpload;
import uk.gov.cslearning.catalogue.dto.upload.Upload;

import java.time.Clock;
import java.time.LocalDateTime;

@Component
public class MediaFactory {

    public Media create(Upload upload) {
        ProcessedFileUpload processedFileUpload = upload.getProcessedFileUpload();
        String ext = processedFileUpload.getFileUpload().getExtension();
        Media media = new Media();
        media.setId(processedFileUpload.getFileUpload().getId());
        media.setContainer(processedFileUpload.getFileUpload().getContainer());
        media.setDateAdded(LocalDateTime.now(Clock.systemUTC()));
        media.setExtension(ext);
        media.setName(processedFileUpload.getFileUpload().getName());
        String path = upload.getPath();
        if (!ext.equals("zip")) {
            path = String.format("%s/%s", upload.getPath(), processedFileUpload.getFileUpload().getName());
        }
        media.setPath(path);
        media.setFileSizeKB(upload.getSizeKB());
        media.setMetadata(processedFileUpload.getMetadata());

        return media;

    }

}
