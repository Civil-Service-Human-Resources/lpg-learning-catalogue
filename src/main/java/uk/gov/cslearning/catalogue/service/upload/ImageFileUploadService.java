package uk.gov.cslearning.catalogue.service.upload;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;
import uk.gov.cslearning.catalogue.service.upload.processor.ImageFileProcessor;

@Service
public class ImageFileUploadService extends FileUploadService {
    public ImageFileUploadService(ImageFileProcessor processor, @Qualifier("existing_container") UploadClient uploadClient) {
        super(processor, uploadClient);
    }

    @Override
    public UploadServiceType getType() {
        return UploadServiceType.IMAGE;
    }
}
