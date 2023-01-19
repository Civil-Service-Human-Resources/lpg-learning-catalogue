package uk.gov.cslearning.catalogue.service.upload.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.UploadableFile;
import uk.gov.cslearning.catalogue.dto.upload.ProcessedFileUpload;
import uk.gov.cslearning.catalogue.exception.FileProcessingException;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class DefaultFileProcessor implements FileProcessor {

    @Override
    public ProcessedFileUpload process(FileUpload fileUpload) throws FileProcessingException {
        try {
            UploadableFile uploadableFile = UploadableFile.createFromFileUpload(fileUpload);
            return new ProcessedFileUpload(fileUpload, Collections.singletonList(uploadableFile));
        } catch (IOException e) {
            log.error(String.format("Failed to process file %s", fileUpload));
            throw new FileProcessingException(e);
        }
    }
}
