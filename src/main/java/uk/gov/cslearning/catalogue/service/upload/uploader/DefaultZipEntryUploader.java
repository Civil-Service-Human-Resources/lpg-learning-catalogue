package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.UploadedFile;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.zip.ZipEntry;

@Component
public class DefaultZipEntryUploader implements ZipEntryUploader {

    public Optional<UploadedFile> upload(UploadClient uploadClient, ZipEntry zipEntry, InputStream inputStream, String path) throws IOException {
        return Optional.of(uploadClient.upload(inputStream, path, getSize(inputStream)));
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
