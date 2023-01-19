package uk.gov.cslearning.catalogue.domain;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.upload.ProcessedFileUpload;
import uk.gov.cslearning.catalogue.dto.upload.Upload;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MediaFactory {

    public Media create(Upload upload) {
        ProcessedFileUpload processedFileUpload = upload.getProcessedFileUpload();
        Media media = new Media();
        media.setId(processedFileUpload.getFileUpload().getId());
        media.setContainer(processedFileUpload.getFileUpload().getContainer());
        media.setDateAdded(LocalDateTime.now(Clock.systemUTC()));
        media.setExtension(processedFileUpload.getFileUpload().getExtension());
        media.setName(processedFileUpload.getFileUpload().getName());
        media.setPath(upload.getPath());
        media.setFileSizeKB(processedFileUpload.getFileUpload().getSizeKB());
        Map<String, Object> convertedMap = processedFileUpload.getMetadata()
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        media.setMetadata(convertedMap);

        return media;

    }

}
