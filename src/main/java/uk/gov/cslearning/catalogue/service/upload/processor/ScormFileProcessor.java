package uk.gov.cslearning.catalogue.service.upload.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.dto.upload.FileUpload;
import uk.gov.cslearning.catalogue.dto.upload.UploadableFile;
import uk.gov.cslearning.catalogue.dto.upload.ProcessedFileUpload;
import uk.gov.cslearning.catalogue.exception.FileProcessingException;
import uk.gov.cslearning.catalogue.exception.InvalidScormException;
import uk.gov.cslearning.catalogue.service.upload.UploadableFileFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
public class ScormFileProcessor implements FileProcessor {

    private final List<String> requiredFiles = Collections.singletonList("imsmanifest.xml");
    private final UploadableFileFactory uploadableFileFactory;

    public ScormFileProcessor(UploadableFileFactory uploadableFileFactory) {
        this.uploadableFileFactory = uploadableFileFactory;
    }

    @Override
    public ProcessedFileUpload process(FileUpload fileUpload) throws FileProcessingException {
        ArrayList<String> validFiles = new ArrayList<>();
        List<UploadableFile> uploadableFiles = new ArrayList<>();
        try (ZipInputStream inputStream = new ZipInputStream(fileUpload.getFile().getInputStream())) {
            ZipEntry zipEntry = inputStream.getNextEntry();

            while (zipEntry != null) {
                if (!zipEntry.isDirectory()) {
                    String filename = zipEntry.getName();
                    if (requiredFiles.contains(filename)) {
                        validFiles.add(filename);
                    }
                    UploadableFile uploadableFile = uploadableFileFactory.create(filename, fileUpload.getDestination(), inputStream);
                    uploadableFiles.add(uploadableFile);
                }
                zipEntry = inputStream.getNextEntry();
            }
            List<String> missingFiles = requiredFiles.stream().filter(rF -> !validFiles.contains(rF)).collect(Collectors.toList());
            if (missingFiles.size() > 0) {
                throw new InvalidScormException(String.format("SCORM file is missing the following required files: %s", String.join(",", missingFiles)));
            }
        } catch (IOException | InvalidScormException e) {
            log.error(String.format("Error processing SCORM package: %s", fileUpload), e);
            throw new FileProcessingException(e);
        }
        return new ProcessedFileUpload(fileUpload, uploadableFiles);
    }
}
