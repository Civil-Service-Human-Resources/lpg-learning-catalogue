package uk.gov.cslearning.catalogue.service;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.media.Media;
import uk.gov.cslearning.catalogue.domain.media.MediaEntityFactory;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.exception.FileUploadException;
import uk.gov.cslearning.catalogue.repository.MediaRepository;

import java.io.IOException;
import java.net.URISyntaxException;

@Service
public class AzureMediaManagementService implements MediaManagementService {

    private final CloudBlobClient azureClient;
    private final MediaEntityFactory mediaFactory;
    private final MediaRepository mediaRepository;
    private final String storageContainerName;

    public AzureMediaManagementService(CloudBlobClient azureClient, MediaEntityFactory mediaFactory, MediaRepository mediaRepository, @Value("${azure.storage.container}") String storageContainerName) {
        this.azureClient = azureClient;
        this.mediaFactory = mediaFactory;
        this.mediaRepository = mediaRepository;
        this.storageContainerName = storageContainerName;
    }

    @Override
    public Media create(FileUpload fileUpload) {

        String filePath = String.join("/", fileUpload.getContainer(), fileUpload.getName());

        try {
            CloudBlobContainer container = azureClient.getContainerReference(storageContainerName);
            container.createIfNotExists();
            CloudBlockBlob blob = container.getBlockBlobReference(filePath);
            blob.upload(fileUpload.getFile().getInputStream(), fileUpload.getFile().getSize());

            return mediaRepository.save(mediaFactory.create(fileUpload));

        } catch (URISyntaxException | StorageException | IOException e) {
            throw new FileUploadException(e);
        }
    }
}
