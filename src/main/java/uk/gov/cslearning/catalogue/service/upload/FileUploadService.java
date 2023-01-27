package uk.gov.cslearning.catalogue.service.upload;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.gov.cslearning.catalogue.dto.upload.*;
import uk.gov.cslearning.catalogue.exception.FileUploadException;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;
import uk.gov.cslearning.catalogue.service.upload.processor.FileProcessor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public abstract class FileUploadService {

    protected FileProcessor processor;
    protected UploadClient uploadClient;

    public FileUploadService(FileProcessor processor, @Qualifier("existing_container") UploadClient uploadClient) {
        this.processor = processor;
        this.uploadClient = uploadClient;
    }

    private List<Throwable> extractErrorsFromUploads(List<UploadedFile> uploadedFiles) {
        List<Throwable> errors = Collections.emptyList();
        List<UploadedFile> failedUploads = uploadedFiles
                .stream().filter(u -> u.getStatus().equals(UploadStatus.FAIL))
                .collect(Collectors.toList());
        if (failedUploads.size() > 0) {
            errors = failedUploads.stream().map(UploadedFile::getException).collect(Collectors.toList());
        }
        return errors;
    }

    public Upload upload(FileUpload fileUpload) {
        ProcessedFileUpload processedFileUpload = processor.process(fileUpload);
        List<UploadableFile> uploadableFiles = processedFileUpload.getUploadableFiles();
        List<UploadedFile> uploadedFiles = uploadableFiles
                .stream().map(uploadClient::upload).collect(Collectors.toList());
        List<Throwable> errors = extractErrorsFromUploads(uploadedFiles);
        if (!errors.isEmpty()) {
            String errMsg = String.format(
                    "Error(s) uploading file %s: %s",
                    fileUpload,
                    errors.stream().map(Throwable::getMessage).collect(Collectors.joining("\n"))
            );
            throw new FileUploadException(errMsg);
        }
        return new Upload(processedFileUpload, uploadedFiles, fileUpload.getDestination());
    }

    public void delete(String filePath) {

        String[] items = filePath.split("/");

        if (items.length > 3) {
            String relativePath = items[items.length - 3] + "/" + items[items.length - 2] + "/" + items[items.length - 1];
            uploadClient.delete(relativePath);
        }
    }

    public void deleteDirectory(String filePath) {

        String[] items = filePath.split("/");
        String relativePath = items[items.length - 2] + "/" + items[items.length - 1];

        uploadClient.deleteDirectory(relativePath);
    }

    abstract public UploadServiceType getType();
}
