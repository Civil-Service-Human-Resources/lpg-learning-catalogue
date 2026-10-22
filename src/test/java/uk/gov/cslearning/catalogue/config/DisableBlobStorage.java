package uk.gov.cslearning.catalogue.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;

@Configuration
public class DisableBlobStorage {

    @Bean(name = "learning_material")
    public UploadClient scormMaterialClient() {
        return Mockito.mock(UploadClient.class);
    }

    @Bean(name = "existing_container")
    public UploadClient existingMaterialClient() {
        return Mockito.mock(UploadClient.class);
    }
}
