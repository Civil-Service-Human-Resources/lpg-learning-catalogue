package uk.gov.cslearning.catalogue.service.upload.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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

    private final List<String> validManifests;
    private final UploadableFileFactory uploadableFileFactory;

    public ScormFileProcessor(UploadableFileFactory uploadableFileFactory,
                              @Qualifier("elearning_manifest_list") List<String> validManifests) {
        this.uploadableFileFactory = uploadableFileFactory;
        this.validManifests = validManifests;
    }

    private String fetchManifest(List<String> filenamesInZip) {
        List<String> manifests = validManifests.stream().filter(filenamesInZip::contains).collect(Collectors.toList());
        if (manifests.isEmpty()) {
            throw new InvalidScormException(String.format("SCORM file is missing a manifest. Possible manifests are: %s", String.join(",", validManifests)));
        }
        if (manifests.size() > 1) {
            throw new InvalidScormException(String.format("SCORM file has more than one valid manifest. Possible manifests are: %s", String.join(",", validManifests)));
        }
        return manifests.get(0);
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
