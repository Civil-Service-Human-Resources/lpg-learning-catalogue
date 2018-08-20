package uk.gov.cslearning.catalogue.service;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.media.Media;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.factory.MediaFactory;
import uk.gov.cslearning.catalogue.exception.FileUploadException;
import uk.gov.cslearning.catalogue.repository.MediaRepository;

import java.io.IOException;
import java.net.URISyntaxException;

@Service
public class AzureMediaManagementService implements MediaManagementService {

    private final CloudBlobClient azureClient;
    private final MediaFactory mediaFactory;
    private final MediaRepository mediaRepository;

    @Autowired
    public AzureMediaManagementService(CloudBlobClient azureClient, MediaFactory mediaFactory, MediaRepository mediaRepository) {
        this.azureClient = azureClient;
        this.mediaFactory = mediaFactory;
        this.mediaRepository = mediaRepository;
    }

    @Override
    public Media create(FileUpload fileUpload) {

        try {
            CloudBlobContainer container = azureClient.getContainerReference(fileUpload.getContainer());
            container.createIfNotExists();
            CloudBlockBlob blob = container.getBlockBlobReference(fileUpload.getName());
            blob.upload(fileUpload.getFile().getInputStream(), fileUpload.getFile().getSize());
            return mediaRepository.save(mediaFactory.create(fileUpload));

        } catch (URISyntaxException | StorageException | IOException e) {
            throw new FileUploadException(e);
        }
    }
}
