package uk.gov.cslearning.catalogue.service.upload.processor;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.exception.FileUploadException;
import uk.gov.cslearning.catalogue.exception.InvalidScormException;
import uk.gov.cslearning.catalogue.service.upload.InputStreamFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
@Component
public class ScormFileProcessor implements FileProcessor {
    private final InputStreamFactory inputStreamFactory;

    private final List<String> requiredFiles = Collections.singletonList("imsmanifest.xml");
    public ScormFileProcessor(InputStreamFactory inputStreamFactory) {
        this.inputStreamFactory = inputStreamFactory;
    }
    @Override
    public ProcessedFile process(FileUpload fileUpload) {
        try (ZipInputStream inputStream = inputStreamFactory.createZipInputStream(fileUpload.getFile().getInputStream())){
            ArrayList<String> validFiles = new ArrayList<>();
            Map<String, String> metadata = Collections.emptyMap();
            ZipEntry zipEntry = inputStream.getNextEntry();
            while (zipEntry != null) {
                if (requiredFiles.contains(zipEntry.getName())) {
                    validFiles.add(zipEntry.getName());
                }
                zipEntry = inputStream.getNextEntry();
            }
            List<String> missingFiles = requiredFiles.stream().filter(rF -> !validFiles.contains(rF)).collect(Collectors.toList());
            if (missingFiles.size() > 0) {
                throw new InvalidScormException(String.format("SCORM file is missing the following required files: %s", String.join(",", missingFiles)));
            }
            return ProcessedFile.createWithMetadata(fileUpload, metadata);
        } catch (IOException | InvalidScormException e) {
            throw new FileUploadException(e);
        }
    }
}
