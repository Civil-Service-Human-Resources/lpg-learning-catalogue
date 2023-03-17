package uk.gov.cslearning.catalogue.service.upload.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.CustomMediaMetadata;
import uk.gov.cslearning.catalogue.dto.upload.FileUpload;
import uk.gov.cslearning.catalogue.dto.upload.ProcessedFileUpload;
import uk.gov.cslearning.catalogue.dto.upload.UploadableFile;
import uk.gov.cslearning.catalogue.exception.FileProcessingException;
import uk.gov.cslearning.catalogue.exception.InvalidScormException;
import uk.gov.cslearning.catalogue.service.upload.UploadableFileFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ScormFileProcessor implements FileProcessor {
    private final ELearningManifestService eLearningManifestService;
    private final UploadableFileFactory uploadableFileFactory;

    public ScormFileProcessor(UploadableFileFactory uploadableFileFactory,
                              ELearningManifestService eLearningManifestService) {
        this.uploadableFileFactory = uploadableFileFactory;
        this.eLearningManifestService = eLearningManifestService;
    }

    private String fetchManifest(List<String> filenamesInZip) {
        String manifest = eLearningManifestService.fetchManifestFromFileList(filenamesInZip);
        if (manifest == null) {
            throw new InvalidScormException("ELearning package does not contain a valid manifest");
        }
        return manifest;
    }

    @Override
    public ProcessedFileUpload process(FileUpload fileUpload) throws FileProcessingException {
        List<UploadableFile> uploadableFiles;
        try {
            uploadableFiles = uploadableFileFactory.createFromZip(fileUpload);
            String manifest = fetchManifest(uploadableFiles.stream().map(UploadableFile::getName).collect(Collectors.toList()));
            Map<String, String> metadata = Collections.singletonMap(CustomMediaMetadata.ELEARNING_MANIFEST.getMetadataKey(), manifest);
            return new ProcessedFileUpload(fileUpload, uploadableFiles, metadata);
        } catch (IOException | InvalidScormException e) {
            log.error(String.format("Error processing SCORM package: %s", fileUpload), e);
            throw new FileProcessingException(e);
        }
    }
}
