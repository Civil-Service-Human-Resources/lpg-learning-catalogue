package uk.gov.cslearning.catalogue.service.upload;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;
import uk.gov.cslearning.catalogue.service.upload.processor.ScormFileProcessor;

@Service
public class ScormFileUploadService extends FileUploadService {
    public ScormFileUploadService(ScormFileProcessor processor, @Qualifier("learning_material") UploadClient uploadClient) {
        super(processor, uploadClient);
    }

    @Override
    public UploadServiceType getType() {
        return UploadServiceType.SCORM;
    }
}
