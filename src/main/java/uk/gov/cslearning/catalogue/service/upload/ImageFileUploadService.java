package uk.gov.cslearning.catalogue.service.upload;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;
import uk.gov.cslearning.catalogue.service.upload.processor.ImageFileProcessor;

@Service
public class ImageFileUploadService extends FileUploadService {
    public ImageFileUploadService(ImageFileProcessor processor, UploadClient uploadClient) {
        super(processor, uploadClient);
    }
}
