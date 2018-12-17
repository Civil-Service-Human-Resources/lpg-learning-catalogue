package uk.gov.cslearning.catalogue.service.upload.client;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.UploadedFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

@Component
public class AzureUploadClient implements UploadClient {
    private static final Logger LOG = LoggerFactory.getLogger(AzureUploadClient.class);
    private final CloudBlobClient azureClient;
    private final String storageContainerName;
    private final UploadedFileFactory uploadedFileFactory;

    public AzureUploadClient(CloudBlobClient azureClient,
                             @Value("${azure.storage.container}") String storageContainerName,
                             UploadedFileFactory uploadedFileFactory) {
        this.azureClient = azureClient;
        this.storageContainerName = storageContainerName;
        this.uploadedFileFactory = uploadedFileFactory;
    }

    @Override
    public UploadedFile upload(InputStream inputStream, String filePath, long fileSizeBytes) {
        return upload(inputStream, filePath, fileSizeBytes, "application/octet-stream");
    }

    @Override
    public UploadedFile upload(InputStream inputStream, String filePath, long fileSizeBytes, String contentType) {

        try {
            CloudBlobContainer container = azureClient.getContainerReference(storageContainerName);
            container.createIfNotExists();

            CloudBlockBlob blob = container.getBlockBlobReference(filePath);
            blob.getProperties().setContentType(contentType);
            blob.upload(inputStream, fileSizeBytes);

            return uploadedFileFactory.successulUploadedFile(filePath, fileSizeBytes);
        } catch (StorageException | URISyntaxException | IOException e) {
            LOG.error("Unable to upload file", e);
            return uploadedFileFactory.failedUploadedFile(filePath, fileSizeBytes, e);
        }
    }

    @Override
    public void delete(String filePath) {
        try {
            CloudBlobContainer container = azureClient.getContainerReference(storageContainerName);
            CloudBlockBlob blob = container.getBlockBlobReference(filePath);
            blob.deleteIfExists();
        } catch (StorageException | URISyntaxException e) {
            LOG.error("Unable to delete file", e);
        }
    }

    @Override
    public void deleteDirectory(String filePath) {
        try {
            CloudBlobContainer container = azureClient.getContainerReference(storageContainerName);
            CloudBlobDirectory directory = container.getDirectoryReference(filePath);
            for (ListBlobItem blob : directory.listBlobsSegmented().getResults()) {
                if (blob instanceof CloudBlobDirectory) {
                    String items[] = blob.getUri().getPath().split("/");
                    String path = filePath + "/" + items[items.length - 1];
                    deleteDirectory(path);
                } else {
                    ((CloudBlockBlob) blob).deleteIfExists();
                }
            }
        } catch (StorageException | URISyntaxException e) {
            LOG.error("Unable to delete folder", e);
        }
    }
}
