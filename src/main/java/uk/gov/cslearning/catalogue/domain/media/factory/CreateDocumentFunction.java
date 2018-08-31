package uk.gov.cslearning.catalogue.domain.media.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.domain.media.Document;
import uk.gov.cslearning.catalogue.domain.media.MediaEntity;
import uk.gov.cslearning.catalogue.dto.Upload;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.function.Function;

@Component
public class CreateDocumentFunction implements Function<Upload, MediaEntity> {
    @Override
    public MediaEntity apply(Upload upload) {
        Document document = new Document();
        document.setId(upload.getProcessedFile().getFileUpload().getId());
        document.setContainer(upload.getProcessedFile().getFileUpload().getContainer());
        document.setDateAdded(LocalDateTime.now(Clock.systemUTC()));
        document.setExtension(upload.getProcessedFile().getFileUpload().getExtension());
        document.setName(upload.getProcessedFile().getFileUpload().getName());
        document.setPath(upload.getPath());
        document.setFileSize(upload.getSizeKB());
        document.setMetadata(upload.getProcessedFile().getMetadata());

        return document;
    }
}
