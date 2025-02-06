package uk.gov.cslearning.catalogue.service.upload.client;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;
import lombok.extern.slf4j.Slf4j;
import uk.gov.cslearning.catalogue.dto.upload.UploadableFile;
import uk.gov.cslearning.catalogue.dto.upload.UploadedFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

@Slf4j
public class AzureUploadClient implements UploadClient {
    private final CloudBlobContainer container;

    public AzureUploadClient(CloudBlobContainer container) {
        this.container = container;
    }

    @Override
    public UploadedFile upload(UploadableFile file) {
        log.debug(String.format("Uploading file %s", file.getFullPath()));
        String filePath = file.getFullPath();
        long fileSizeBytes = file.getFileSize();
        long fileSizeInKB = fileSizeBytes / 1024;
        try(InputStream byteInputStream = file.getInputStream()) {
            CloudBlockBlob blob = container.getBlockBlobReference(filePath);
            blob.getProperties().setContentType(file.getContentType());
            blob.upload(byteInputStream, fileSizeBytes);
            return UploadedFile.createSuccessfulUploadedFile(fileSizeInKB, filePath);
        } catch (StorageException | URISyntaxException | IOException e) {
            log.error(String.format("Encountered error uploading file: %s", e));
            return UploadedFile.createFailedUploadedFile(fileSizeBytes / 1024, filePath, e);
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
