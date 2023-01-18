package uk.gov.cslearning.catalogue.service.upload.client;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.UploadableFile;
import uk.gov.cslearning.catalogue.dto.UploadedFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

@Component
@Slf4j
public class AzureUploadClient implements UploadClient {
    private final CloudBlobClient azureClient;
    private final String storageContainerName;

    public AzureUploadClient(CloudBlobClient azureClient,
                             @Value("${azure.storage.container}") String storageContainerName) {
        this.azureClient = azureClient;
        this.storageContainerName = storageContainerName;
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

            return UploadedFile.createSuccessulUploadedFile(filePath, fileSizeBytes);
        } catch (StorageException | URISyntaxException | IOException e) {
            log.error("Unable to upload file", e);
            return UploadedFile.createFailedUploadedFile(filePath, fileSizeBytes, e);
        }
    }

    @Override
    public UploadedFile upload(UploadableFile file, String destinationDirectory) {
        String filePath = String.join("/", destinationDirectory, file.getName());
        return upload(file.getAsByteArrayInputStream(), filePath, file.getBytes().length, file.getContentType());
    }

    @Override
    public void delete(String filePath) {
        try {
            CloudBlobContainer container = azureClient.getContainerReference(storageContainerName);
            CloudBlockBlob blob = container.getBlockBlobReference(filePath);
            blob.deleteIfExists();
        } catch (StorageException | URISyntaxException e) {
            log.error("Unable to delete file", e);
        }
    }

    @Override
    public void deleteDirectory(String filePath) {
        try {
            CloudBlobContainer container = azureClient.getContainerReference(storageContainerName);
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
