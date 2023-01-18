package uk.gov.cslearning.catalogue.service.upload.client;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.UploadableFile;
import uk.gov.cslearning.catalogue.dto.UploadedFile;

import java.io.InputStream;

@Component
public interface UploadClient {

    UploadedFile upload(InputStream inputStream, String filePath, long fileSizeBytes);

    UploadedFile upload(InputStream inputStream, String filePath, long fileSizeBytes, String contentType);

    UploadedFile upload(UploadableFile file, String destinationDirectory);

    void delete(String filePath);

    void deleteDirectory(String filePath);
}
