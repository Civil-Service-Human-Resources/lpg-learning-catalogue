package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.dto.UploadedFile;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;

import java.io.IOException;
import java.util.Collections;

@Component
public class DefaultUploader implements Uploader {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultUploader.class);
    private final UploadFactory uploadFactory;

    public DefaultUploader(UploadFactory uploadFactory) {
        this.uploadFactory = uploadFactory;
    }

    @Override
    public Upload upload(ProcessedFile processedFile, UploadClient uploadClient) {
        String filePath = String.join("/", processedFile.getFileUpload().getContainer(), processedFile.getFileUpload().getId());

        try {
            UploadedFile uploadedFile = uploadClient.upload(processedFile.getFileUpload().getFile().getInputStream(),
                    filePath, processedFile.getFileUpload().getFile().getSize());

            return uploadFactory.createUpload(processedFile, Collections.singletonList(uploadedFile), filePath);

        } catch (IOException e) {
            LOG.error(String.format("Upload failed: %s", processedFile), e);
            return uploadFactory.createFailedUpload(processedFile, filePath, e);
        }
    }
}
