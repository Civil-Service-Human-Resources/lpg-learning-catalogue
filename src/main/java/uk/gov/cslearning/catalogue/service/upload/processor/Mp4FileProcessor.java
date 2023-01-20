package uk.gov.cslearning.catalogue.service.upload.processor;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.upload.FileUpload;
import uk.gov.cslearning.catalogue.dto.upload.UploadableFile;
import uk.gov.cslearning.catalogue.dto.upload.ProcessedFileUpload;
import uk.gov.cslearning.catalogue.exception.FileUploadException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

@Component
public class Mp4FileProcessor implements FileProcessor {
    private final MetadataParser metadataParser;

    public Mp4FileProcessor(MetadataParser metadataParser) {
        this.metadataParser = metadataParser;
    }

    @Override
    public ProcessedFileUpload process(FileUpload fileUpload) {
        try (InputStream inputStream = fileUpload.getFile().getInputStream()) {
            Map<String, String> metadata = metadataParser.parse(inputStream);
            UploadableFile uploadableFile = UploadableFile.createFromFileUpload(fileUpload);
            return new ProcessedFileUpload(fileUpload, Collections.singletonList(uploadableFile), metadata);
        } catch (IOException e) {
            throw new FileUploadException(e);
        }

    }
}
