package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.dto.UploadedFile;
import uk.gov.cslearning.catalogue.service.upload.FileFactory;
import uk.gov.cslearning.catalogue.service.upload.InputStreamFactory;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class ScormUploader implements Uploader {
    private static final Logger LOG = LoggerFactory.getLogger(ScormUploader.class);
    private final InputStreamFactory inputStreamFactory;
    private final UploadFactory uploadFactory;
    private FileFactory fileFactory;
    private final Map<String, String> fileSubstitutions;

    public ScormUploader(InputStreamFactory inputStreamFactory, UploadFactory uploadFactory, FileFactory fileFactory, @Qualifier("fileSubstitions") Map<String, String> fileSubstitutions) {
        this.inputStreamFactory = inputStreamFactory;
        this.uploadFactory = uploadFactory;
        this.fileFactory = fileFactory;
        this.fileSubstitutions = fileSubstitutions;
    }

    @Override
    public Upload upload(ProcessedFile processedFile, UploadClient uploadClient) {
        List<UploadedFile> uploadedFiles = new ArrayList<>();

        String destinationDirectory = String.join("/", processedFile.getFileUpload().getContainer(), processedFile.getFileUpload().getId());

        try (ZipInputStream inputStream = inputStreamFactory.createZipInputStream(processedFile.getFileUpload().getFile().getInputStream())) {
            ZipEntry zipEntry = inputStream.getNextEntry();

            while (zipEntry != null) {
                String filePath = String.join("/", destinationDirectory, zipEntry.getName());

                if (fileSubstitutions.containsKey(zipEntry.getName())) {
                    if (!fileSubstitutions.get(zipEntry.getName()).isEmpty()) {
                        File file = fileFactory.get(fileSubstitutions.get(zipEntry.getName()));
                        try (InputStream fileInputStream = inputStreamFactory.createFileInputStream(file)) {
                            uploadedFiles.add(uploadClient.upload(fileInputStream, filePath, file.length()));
                        }
                    }
                }
                else {
                    long length;
                    long size = 0;
                    while ((length = inputStream.read(new byte[1024])) > 0) {
                        size += length;
                    }

                    uploadedFiles.add(uploadClient.upload(inputStream, filePath, size));
                }

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
