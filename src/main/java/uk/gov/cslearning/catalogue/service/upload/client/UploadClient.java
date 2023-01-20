package uk.gov.cslearning.catalogue.service.upload.client;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.upload.UploadableFile;
import uk.gov.cslearning.catalogue.dto.upload.UploadedFile;

@Component
public interface UploadClient {
    UploadedFile upload(UploadableFile file);

    void delete(String filePath);

    void deleteDirectory(String filePath);
}
