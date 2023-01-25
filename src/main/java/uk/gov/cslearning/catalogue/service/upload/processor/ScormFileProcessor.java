package uk.gov.cslearning.catalogue.service.upload.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.dto.upload.FileUpload;
import uk.gov.cslearning.catalogue.dto.upload.ProcessedFileUpload;
import uk.gov.cslearning.catalogue.dto.upload.UploadableFile;
import uk.gov.cslearning.catalogue.exception.FileProcessingException;
import uk.gov.cslearning.catalogue.exception.InvalidScormException;
import uk.gov.cslearning.catalogue.service.upload.UploadableFileFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ScormFileProcessor implements FileProcessor {

    private final List<String> requiredFiles = Collections.singletonList("imsmanifest.xml");
    private final UploadableFileFactory uploadableFileFactory;

    public ScormFileProcessor(UploadableFileFactory uploadableFileFactory) {
        this.uploadableFileFactory = uploadableFileFactory;
    }

    private void validateMissingFiles(List<String> filenamesInZip) {
        List<String> missingFiles = requiredFiles.stream().filter(rF -> !filenamesInZip.contains(rF)).collect(Collectors.toList());
        if (!missingFiles.isEmpty()) {
            throw new InvalidScormException(String.format("SCORM file is missing the following required files: %s", String.join(",", missingFiles)));
        }
    }

    @Override
    public ProcessedFileUpload process(FileUpload fileUpload) throws FileProcessingException {
        List<UploadableFile> uploadableFiles;
        try {
            uploadableFiles = uploadableFileFactory.createFromZip(fileUpload);
            validateMissingFiles(uploadableFiles.stream().map(UploadableFile::getName).collect(Collectors.toList()));
        } catch (IOException | InvalidScormException e) {
            log.error(String.format("Error processing SCORM package: %s", fileUpload), e);
            throw new FileProcessingException(e);
        }
        return new ProcessedFileUpload(fileUpload, uploadableFiles);
    }
}
