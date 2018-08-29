package uk.gov.cslearning.catalogue.service.upload.client;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;

@Component
public class UploadClientFactory {

    private final AzureUploadClient azureUploadClient;

    public UploadClientFactory(AzureUploadClient azureUploadClient) {
        this.azureUploadClient = azureUploadClient;
    }

    public UploadClient create(ProcessedFile processedFile) {
        return azureUploadClient;
    }
}
