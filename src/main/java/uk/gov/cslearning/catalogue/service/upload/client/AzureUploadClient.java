package uk.gov.cslearning.catalogue.service.upload.client;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.UploadedFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

@Component
public class AzureUploadClient implements UploadClient {
    private final CloudBlobClient azureClient;
    private final String storageContainerName;

    public AzureUploadClient(CloudBlobClient azureClient, @Value("${azure.storage.container}")String storageContainerName) {
        this.azureClient = azureClient;
        this.storageContainerName = storageContainerName;
    }

    @Override
    public UploadedFile upload(InputStream inputStream, String filePath, long fileSize) {

        try {
            CloudBlobContainer container = azureClient.getContainerReference(storageContainerName);
            container.createIfNotExists();

            CloudBlockBlob blob = container.getBlockBlobReference(filePath);
            blob.upload(inputStream, fileSize);
        } catch (StorageException | URISyntaxException | IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
