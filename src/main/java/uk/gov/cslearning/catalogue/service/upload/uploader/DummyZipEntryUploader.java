package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.UploadedFile;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.zip.ZipEntry;

@Component
public class DummyZipEntryUploader implements ZipEntryUploader {
    @Override
    public Optional<UploadedFile> upload(UploadClient uploadClient, ZipEntry zipEntry, InputStream inputStream, String path) throws IOException, URISyntaxException {
        return Optional.empty();
    }
}
