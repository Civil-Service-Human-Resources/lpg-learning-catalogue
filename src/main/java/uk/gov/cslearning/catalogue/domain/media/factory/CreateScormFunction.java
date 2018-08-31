package uk.gov.cslearning.catalogue.domain.media.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.domain.media.MediaEntity;
import uk.gov.cslearning.catalogue.domain.media.Scorm;
import uk.gov.cslearning.catalogue.dto.Upload;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.function.Function;

@Component
public class CreateScormFunction implements Function<Upload, MediaEntity> {
    @Override
    public MediaEntity apply(Upload upload) {
        Scorm scorm = new Scorm();
        scorm.setId(upload.getProcessedFile().getFileUpload().getId());
        scorm.setContainer(upload.getProcessedFile().getFileUpload().getContainer());
        scorm.setDateAdded(LocalDateTime.now(Clock.systemUTC()));
        scorm.setExtension(upload.getProcessedFile().getFileUpload().getExtension());
        scorm.setName(upload.getProcessedFile().getFileUpload().getName());
        scorm.setPath(upload.getPath());
        scorm.setFileSize(upload.getSizeKB());
        scorm.setMetadata(upload.getProcessedFile().getMetadata());

        return scorm;
    }
}
