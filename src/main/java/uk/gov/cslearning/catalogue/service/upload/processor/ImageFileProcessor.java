package uk.gov.cslearning.catalogue.service.upload.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.dto.upload.FileUpload;
import uk.gov.cslearning.catalogue.dto.upload.ProcessedFileUpload;
import uk.gov.cslearning.catalogue.dto.upload.UploadableFile;
import uk.gov.cslearning.catalogue.exception.FileProcessingException;
import uk.gov.cslearning.catalogue.exception.FileUploadException;
import uk.gov.cslearning.catalogue.service.upload.UploadableFileFactory;
import uk.gov.cslearning.catalogue.service.upload.processor.validator.ImageFileValidator;

import java.util.Collections;

@Service
@Slf4j
public class ImageFileProcessor implements FileProcessor {

    private final UploadableFileFactory uploadableFileFactory;
    private final ImageFileValidator imageFileValidator;

    public ImageFileProcessor(UploadableFileFactory uploadableFileFactory,
                              ImageFileValidator imageFileValidator) {
        this.uploadableFileFactory = uploadableFileFactory;
        this.imageFileValidator = imageFileValidator;
    }

    @Override
    public ProcessedFileUpload process(FileUpload fileUpload) throws FileProcessingException {
        try {
            imageFileValidator.validate(fileUpload);
            UploadableFile uploadableFile = uploadableFileFactory.createFromFileUpload(fileUpload);
            return new ProcessedFileUpload(fileUpload, Collections.singletonList(uploadableFile));
        } catch (Exception e) {
            log.error(String.format("Failed to process image file \"%s\"", fileUpload.getName()));
            throw new FileUploadException(e);
        }
    }

}
