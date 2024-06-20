package uk.gov.cslearning.catalogue.config;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;

@Configuration
public class DisableBlobStorage {

    @MockBean(name = "learning_material")
    public UploadClient scormMaterialClient;

    @MockBean(name = "existing_container")
    public UploadClient existingMaterialClient;
}
