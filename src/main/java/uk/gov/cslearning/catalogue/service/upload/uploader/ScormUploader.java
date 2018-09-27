package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.dto.UploadedFile;
import uk.gov.cslearning.catalogue.service.upload.InputStreamFactory;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class ScormUploader implements Uploader {
    private static final Logger LOG = LoggerFactory.getLogger(ScormUploader.class);

    private final InputStreamFactory inputStreamFactory;
    private final UploadFactory uploadFactory;
    private final ZipEntryUploaderFactory zipEntryUploaderFactory;

    public ScormUploader(InputStreamFactory inputStreamFactory, UploadFactory uploadFactory, ZipEntryUploaderFactory zipEntryUploaderFactory) {
        this.inputStreamFactory = inputStreamFactory;
        this.uploadFactory = uploadFactory;
        this.zipEntryUploaderFactory = zipEntryUploaderFactory;
    }

    @Override
    public Upload upload(ProcessedFile processedFile, UploadClient uploadClient) {
        List<UploadedFile> uploadedFiles = new ArrayList<>();

        String destinationDirectory = String.join("/", processedFile.getFileUpload().getContainer(), processedFile.getFileUpload().getId());

        try (ZipInputStream inputStream = inputStreamFactory.createZipInputStream(processedFile.getFileUpload().getFile().getInputStream())) {
            ZipEntry zipEntry = inputStream.getNextEntry();

            while (zipEntry != null) {
                String filePath = String.join("/", destinationDirectory, zipEntry.getName());

                ZipEntryUploader zipEntryUploader = zipEntryUploaderFactory.get(zipEntry);
                zipEntryUploader.upload(uploadClient, zipEntry, inputStream, filePath)
                        .ifPresent(uploadedFiles::add);

                zipEntry = inputStream.getNextEntry();
            }

            inputStream.closeEntry();
            return uploadFactory.createUpload(processedFile, uploadedFiles, destinationDirectory);

        } catch (URISyntaxException | IOException e) {
            LOG.error(String.format("Unable to upload Scorm package: %s", processedFile), e);
            return uploadFactory.createFailedUpload(processedFile, destinationDirectory, e);
        }
    }
}