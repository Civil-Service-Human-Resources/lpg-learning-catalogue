package uk.gov.cslearning.catalogue.service.upload.client;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;

@Component
public class UploadClientFactory {

    private final UploadClient azureUploadClient;

    public UploadClientFactory(UploadClient azureUploadClient) {
        this.azureUploadClient = azureUploadClient;
    }

    public UploadClient create(ProcessedFile processedFile) {
        return azureUploadClient;
    }
}
