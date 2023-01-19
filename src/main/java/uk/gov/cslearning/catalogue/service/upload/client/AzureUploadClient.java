package uk.gov.cslearning.catalogue.service.upload.client;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;
import lombok.extern.slf4j.Slf4j;
import uk.gov.cslearning.catalogue.dto.UploadableFile;
import uk.gov.cslearning.catalogue.dto.upload.FailedUploadedFile;
import uk.gov.cslearning.catalogue.dto.upload.SuccessfulUploadedFile;
import uk.gov.cslearning.catalogue.dto.upload.UploadedFile;

import java.io.IOException;
import java.net.URISyntaxException;

@Slf4j
public class AzureUploadClient implements UploadClient {
    private final CloudBlobContainer container;

    public AzureUploadClient(CloudBlobContainer container) {
        this.container = container;
    }

    @Override
    public UploadedFile upload(UploadableFile file) {
        String filePath = file.getFullPath();
        int fileSizeBytes = file.getBytes().length;
        long fileSizeInKB = fileSizeBytes / 1024;
        try {
            CloudBlockBlob blob = container.getBlockBlobReference(filePath);
            blob.getProperties().setContentType(file.getContentType());
            blob.upload(file.getAsByteArrayInputStream(), fileSizeBytes);

            return new SuccessfulUploadedFile(fileSizeInKB, filePath);
        } catch (StorageException | URISyntaxException | IOException e) {
            log.error("Unable to upload file", e);
            return new FailedUploadedFile(fileSizeInKB, filePath, e);
        }
    }

    @Override
    public void delete(String filePath) {
        try {
            CloudBlockBlob blob = container.getBlockBlobReference(filePath);
            blob.deleteIfExists();
        } catch (StorageException | URISyntaxException e) {
            log.error("Unable to delete file", e);
        }
    }

    @Override
    public void deleteDirectory(String filePath) {
        try {
            CloudBlobDirectory directory = container.getDirectoryReference(filePath);
            for (ListBlobItem blob : directory.listBlobsSegmented().getResults()) {
                if (blob instanceof CloudBlobDirectory) {
                    String[] items = blob.getUri().getPath().split("/");
                    String path = filePath + "/" + items[items.length - 1];
                    deleteDirectory(path);
                } else {
                    ((CloudBlockBlob) blob).deleteIfExists();
                }
            }
        } catch (StorageException | URISyntaxException e) {
            log.error("Unable to delete folder", e);
        }
    }
}
