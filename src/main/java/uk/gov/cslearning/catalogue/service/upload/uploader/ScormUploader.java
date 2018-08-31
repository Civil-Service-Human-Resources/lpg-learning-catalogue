package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.dto.UploadedFile;
import uk.gov.cslearning.catalogue.service.upload.ZipInputStreamFactory;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class ScormUploader implements Uploader {
    private static final Logger LOG = LoggerFactory.getLogger(ScormUploader.class);
    private final ZipInputStreamFactory zipInputStreamFactory;
    private final UploadFactory uploadFactory;

    public ScormUploader(ZipInputStreamFactory zipInputStreamFactory, UploadFactory uploadFactory) {
        this.zipInputStreamFactory = zipInputStreamFactory;
        this.uploadFactory = uploadFactory;
    }

    @Override
    public Upload upload(ProcessedFile processedFile, UploadClient uploadClient) {
        List<UploadedFile> uploadedFiles = new ArrayList<>();

        String destinationDirectory = String.join("/", processedFile.getFileUpload().getContainer(), processedFile.getFileUpload().getId());

        try (ZipInputStream inputStream = zipInputStreamFactory.create(processedFile.getFileUpload().getFile().getInputStream())) {
            ZipEntry zipEntry = inputStream.getNextEntry();


            while (zipEntry != null) {
                String filePath = String.join("/", destinationDirectory, zipEntry.getName());

                long length;
                long size = 0;
                while ((length = inputStream.read(new byte[1024])) > 0) {
                    size += length;
                }

                uploadedFiles.add(uploadClient.upload(inputStream, filePath, size));

                zipEntry = inputStream.getNextEntry();
            }

            inputStream.closeEntry();
            return uploadFactory.createUpload(processedFile, uploadedFiles, destinationDirectory);

        } catch (IOException e) {
            LOG.error(String.format("Unable to upload Scorm package: %s", processedFile), e);
            return uploadFactory.createFailedUpload(processedFile, destinationDirectory, e);
        }
    }
}
