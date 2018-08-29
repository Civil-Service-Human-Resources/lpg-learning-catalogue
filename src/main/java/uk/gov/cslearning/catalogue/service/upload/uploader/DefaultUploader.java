package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.dto.UploadedFile;
import uk.gov.cslearning.catalogue.exception.FileUploadException;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;

import java.io.IOException;

@Component
public class DefaultUploader implements Uploader {

    @Override
    public Upload upload(ProcessedFile processedFile, UploadClient uploadClient) {
        String filePath = String.join("/", processedFile.getFileUpload().getContainer(), processedFile.getFileUpload().getId());

        try {
            UploadedFile uploadedFile = uploadClient.upload(processedFile.getFileUpload().getFile().getInputStream(), filePath, processedFile.getFileUpload().getFile().getSize());
            return new Upload();

        } catch (IOException e) {
            throw new FileUploadException(e);
        }
    }
}
