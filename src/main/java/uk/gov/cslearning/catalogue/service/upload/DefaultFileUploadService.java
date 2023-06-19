package uk.gov.cslearning.catalogue.service.upload;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;
import uk.gov.cslearning.catalogue.service.upload.processor.DefaultFileProcessor;

@Service
public class DefaultFileUploadService extends FileUploadService {

    public DefaultFileUploadService(DefaultFileProcessor processor, @Qualifier("existing_container") UploadClient uploadClient) {
        super(processor, uploadClient);
    }

    @Override
    public UploadServiceType getType() {
        return UploadServiceType.FILE;
    }
}
