package uk.gov.cslearning.catalogue.service;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.media.MediaEntity;
import uk.gov.cslearning.catalogue.domain.media.MediaFactory;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.exception.FileUploadException;
import uk.gov.cslearning.catalogue.repository.MediaRepository;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

@Service
public class AzureMediaManagementService implements MediaManagementService {

    private final CloudBlobClient azureClient;
    private final MediaFactory mediaFactory;
    private final MediaRepository mediaRepository;
    private final String storageContainerName;

    public AzureMediaManagementService(CloudBlobClient azureClient, MediaFactory mediaFactory, MediaRepository mediaRepository, @Value("${azure.storage.container}") String storageContainerName) {
        this.azureClient = azureClient;
        this.mediaFactory = mediaFactory;
        this.mediaRepository = mediaRepository;
        this.storageContainerName = storageContainerName;
    }

    @Override
    public MediaEntity create(FileUpload fileUpload) {

        MediaEntity mediaEntity = mediaFactory.create(fileUpload);
        String filePath = String.join("/", fileUpload.getContainer(), mediaEntity.getUid());

        try {
            CloudBlobContainer container = azureClient.getContainerReference(storageContainerName);
            container.createIfNotExists();

            CloudBlockBlob blob = container.getBlockBlobReference(filePath);

            blob.upload(fileUpload.getFile().getInputStream(), fileUpload.getFile().getSize());

            return mediaRepository.save(mediaEntity);

        } catch (URISyntaxException | StorageException | IOException e) {
            throw new FileUploadException(e);
        }
    }

    @Override
    public Optional<MediaEntity> findByUid(String mediaId) {
        return mediaRepository.findById(mediaId);
    }
}
