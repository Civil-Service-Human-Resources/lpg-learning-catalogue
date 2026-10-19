package uk.gov.cslearning.catalogue.config;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Configuration;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;

@Configuration
public class DisableBlobStorage {

    @MockitoBean(name = "learning_material")
    public UploadClient scormMaterialClient;

    @MockitoBean(name = "existing_container")
    public UploadClient existingMaterialClient;
}
