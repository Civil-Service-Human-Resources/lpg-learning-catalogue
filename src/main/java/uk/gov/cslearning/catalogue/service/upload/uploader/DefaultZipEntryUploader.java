package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.UploadedFile;
import uk.gov.cslearning.catalogue.service.upload.InputStreamFactory;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;
import uk.gov.cslearning.catalogue.service.upload.processor.MetadataParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.zip.ZipEntry;

@Component
public class DefaultZipEntryUploader implements ZipEntryUploader {
    private final MetadataParser metadataParser;
    private final InputStreamFactory inputStreamFactory;

    public DefaultZipEntryUploader(MetadataParser metadataParser, InputStreamFactory inputStreamFactory) {
        this.metadataParser = metadataParser;
        this.inputStreamFactory = inputStreamFactory;
    }

    public Optional<UploadedFile> upload(UploadClient uploadClient, ZipEntry zipEntry, InputStream inputStream, String path) throws IOException {
        if (!zipEntry.isDirectory()) {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            String contentType = metadataParser.getContentType(inputStreamFactory.createByteArrayInputStream(bytes), zipEntry.getName());
            return Optional.of(uploadClient.upload(inputStreamFactory.createByteArrayInputStream(bytes), path, bytes.length, contentType));
        }
        return Optional.empty();
    }

    public long getSize(InputStream inputStream) throws IOException {
        long length;
        long size = 0;
        while ((length = inputStream.read(new byte[1024])) > 0) {
            size += length;
        }

        return size;
    }
}
