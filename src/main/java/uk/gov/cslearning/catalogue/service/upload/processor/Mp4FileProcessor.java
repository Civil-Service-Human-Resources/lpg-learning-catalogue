package uk.gov.cslearning.catalogue.service.upload.processor;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.ProcessedFileFactory;
import uk.gov.cslearning.catalogue.exception.FileUploadException;

import java.io.IOException;
import java.io.InputStream;

@Component
public class Mp4FileProcessor implements FileProcessor {
    private final ProcessedFileFactory processedFileFactory;
    private final MetadataParser metadataParser;

    public Mp4FileProcessor(ProcessedFileFactory processedFileFactory, MetadataParser metadataParser) {
        this.processedFileFactory = processedFileFactory;
        this.metadataParser = metadataParser;
    }

    @Override
    public ProcessedFile process(FileUpload fileUpload) {
        try (InputStream inputStream = fileUpload.getFile().getInputStream()) {
            return processedFileFactory.create(fileUpload, metadataParser.parse(inputStream));
        } catch (IOException e) {
            throw new FileUploadException(e);
        }
    }
}
