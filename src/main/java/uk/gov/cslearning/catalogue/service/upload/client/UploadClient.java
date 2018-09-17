package uk.gov.cslearning.catalogue.service.upload.client;

import uk.gov.cslearning.catalogue.dto.UploadedFile;

import java.io.InputStream;

public interface UploadClient {

    UploadedFile upload(InputStream inputStream, String filePath, long fileSizeBytes);

    UploadedFile upload(InputStream inputStream, String filePath, long fileSizeBytes, String contentType);
}
