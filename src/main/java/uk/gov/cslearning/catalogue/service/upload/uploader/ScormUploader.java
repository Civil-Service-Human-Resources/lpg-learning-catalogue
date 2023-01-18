package uk.gov.cslearning.catalogue.service.upload.uploader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.dto.UploadableFile;
import uk.gov.cslearning.catalogue.dto.UploadedFile;
import uk.gov.cslearning.catalogue.dto.factory.UploadableFileFactory;
import uk.gov.cslearning.catalogue.exception.InvalidScormException;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
@Slf4j
public class ScormUploader implements Uploader {
    private final List<String> requiredFiles = Collections.singletonList("imsmanifest.xml");
    private final UploadableFileFactory uploadableFileFactory;
    private final UploadClient uploadClient;

    public ScormUploader(UploadClient uploadClient, UploadableFileFactory uploadableFileFactory) {
        this.uploadClient = uploadClient;
        this.uploadableFileFactory = uploadableFileFactory;
    }

    private List<UploadableFile> validateAndFetchFiles(ProcessedFile processedFile) throws IOException, InvalidScormException {
        ArrayList<String> validFiles = new ArrayList<>();
        List<UploadableFile> uploadableFiles = new ArrayList<>();
        try (ZipInputStream inputStream = new ZipInputStream(processedFile.getFileUpload().getFile().getInputStream())) {
            ZipEntry zipEntry = inputStream.getNextEntry();

            while (zipEntry != null) {
                if (!zipEntry.isDirectory()) {
                    if (requiredFiles.contains(zipEntry.getName())) {
                        validFiles.add(zipEntry.getName());
                    }
                    UploadableFile uploadableFile = uploadableFileFactory.create(zipEntry.getName(), inputStream);
                    uploadableFiles.add(uploadableFile);
                }
                zipEntry = inputStream.getNextEntry();
            }
            List<String> missingFiles = requiredFiles.stream().filter(rF -> !validFiles.contains(rF)).collect(Collectors.toList());
            if (missingFiles.size() > 0) {
                throw new InvalidScormException(String.format("SCORM file is missing the following required files: %s", String.join(",", missingFiles)));
            }
        } catch (IOException e) {
            log.error(String.format("Error processing SCORM package: %s", processedFile), e);
            throw e;
        }
        return uploadableFiles;
    }

    @Override
    public Upload upload(ProcessedFile processedFile) {
        List<UploadedFile> uploadedFiles = new ArrayList<>();
        String destinationDirectory = String.join("/", processedFile.getFileUpload().getContainer(), processedFile.getFileUpload().getId());
        try {
            List<UploadableFile> uploadableFiles = validateAndFetchFiles(processedFile);
            uploadableFiles.stream().map(f -> uploadClient.upload(f, destinationDirectory)).forEach(uploadedFiles::add);
        } catch (IOException e) {
            log.error(String.format("Unable to upload Scorm package: %s", processedFile), e);
            return Upload.createFailedUpload(processedFile, destinationDirectory, e);
        }
        return Upload.createSuccessfulUpload(processedFile, uploadedFiles, destinationDirectory);
    }
}
