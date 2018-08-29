package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.dto.UploadStatus;
import uk.gov.cslearning.catalogue.service.upload.ZipInputStreamFactory;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class ScormUploader implements Uploader {
    private static final Logger LOG = LoggerFactory.getLogger(ScormUploader.class);
    private ZipInputStreamFactory zipInputStreamFactory;

    public ScormUploader(ZipInputStreamFactory zipInputStreamFactory) {
        this.zipInputStreamFactory = zipInputStreamFactory;
    }

    @Override
    public Upload upload(ProcessedFile processedFile, UploadClient uploadClient) {
        Upload upload = new Upload();

        try (ZipInputStream inputStream = zipInputStreamFactory.create(processedFile.getFileUpload().getFile().getInputStream())){
            ZipEntry zipEntry = inputStream.getNextEntry();

            String filePath = String.join("/", processedFile.getFileUpload().getContainer(), processedFile.getFileUpload().getId(), zipEntry.getName());

            while (zipEntry != null) {
                long length;
                long size = 0;
                while ((length = inputStream.read(new byte[1024])) > 0) {
                    size += length;
                }

                upload.addToUploadedFiles(uploadClient.upload(inputStream, filePath, size));
                upload.setStatus(UploadStatus.OK);

                zipEntry = inputStream.getNextEntry();
            }

            inputStream.closeEntry();
        } catch (IOException e) {
            LOG.error("Unable to upload Scorm package", e);
            upload.setStatus(UploadStatus.FAIL);
            upload.setError(e);
        }

        return upload;
    }
}
