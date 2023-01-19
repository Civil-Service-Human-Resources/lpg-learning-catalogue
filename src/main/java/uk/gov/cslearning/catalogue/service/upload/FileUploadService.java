package uk.gov.cslearning.catalogue.service.upload;

import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.UploadStatus;
import uk.gov.cslearning.catalogue.dto.UploadableFile;
import uk.gov.cslearning.catalogue.dto.upload.*;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;
import uk.gov.cslearning.catalogue.service.upload.processor.FileProcessor;

import java.util.List;
import java.util.stream.Collectors;

public abstract class FileUploadService {

    protected FileProcessor processor;
    protected UploadClient uploadClient;

    public FileUploadService(FileProcessor processor, UploadClient uploadClient) {
        this.processor = processor;
        this.uploadClient = uploadClient;
    }
    public Upload upload(FileUpload fileUpload) {
        ProcessedFileUpload processedFileUpload = processor.process(fileUpload);
        List<UploadableFile> uploadableFiles = processedFileUpload.getUploadableFiles();
        List<UploadedFile> uploadedFiles = uploadableFiles
                .stream().map(uploadClient::upload).collect(Collectors.toList());
        List<FailedUploadedFile> failedUploads = uploadedFiles
                .stream().filter(u -> u.getStatus().equals(UploadStatus.FAIL))
                .map(u -> (FailedUploadedFile) u).collect(Collectors.toList());
        if (failedUploads.size() > 0) {
            List<Throwable> errors = failedUploads.stream().map(FailedUploadedFile::getError).collect(Collectors.toList());
            return new FailedUpload(processedFileUpload, errors, fileUpload.getDestination());
        }
        return new SuccessfulUpload(processedFileUpload, fileUpload.getDestination(), uploadedFiles);
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
}
