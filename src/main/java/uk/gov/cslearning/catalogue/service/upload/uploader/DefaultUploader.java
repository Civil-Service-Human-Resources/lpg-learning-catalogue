package uk.gov.cslearning.catalogue.service.upload.uploader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.dto.UploadedFile;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class DefaultUploader implements Uploader {

    private final UploadClient uploadClient;

    public DefaultUploader(UploadClient uploadClient) {
        this.uploadClient = uploadClient;
    }

    @Override
    public Upload upload(ProcessedFile processedFile) {
        String filePath = String.join("/", processedFile.getFileUpload().getContainer(), processedFile.getFileUpload().getId(), processedFile.getFileUpload().getName());

        try {
            UploadedFile uploadedFile = uploadClient.upload(processedFile.getFileUpload().getFile().getInputStream(),
                    filePath, processedFile.getFileUpload().getFile().getSize(), processedFile.getFileUpload().getFile().getContentType());

            return Upload.createSuccessfulUpload(processedFile, Collections.singletonList(uploadedFile), filePath);

        } catch (IOException e) {
            log.error(String.format("Upload failed: %s", processedFile), e);
            return Upload.createFailedUpload(processedFile, filePath, e);
        }
    }
}
