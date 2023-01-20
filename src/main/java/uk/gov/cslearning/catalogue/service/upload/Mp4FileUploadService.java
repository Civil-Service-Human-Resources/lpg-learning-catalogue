package uk.gov.cslearning.catalogue.service.upload;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;
import uk.gov.cslearning.catalogue.service.upload.processor.Mp4FileProcessor;

@Service
public class Mp4FileUploadService extends FileUploadService {
    public Mp4FileUploadService(Mp4FileProcessor processor, UploadClient uploadClient) {
        super(processor, uploadClient);
    }

    @Override
    public UploadServiceType getType() {
        return UploadServiceType.MP4;
    }
}
