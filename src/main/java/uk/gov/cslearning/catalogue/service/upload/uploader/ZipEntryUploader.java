package uk.gov.cslearning.catalogue.service.upload.uploader;

import uk.gov.cslearning.catalogue.dto.UploadedFile;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.zip.ZipEntry;

public interface ZipEntryUploader {
    Optional<UploadedFile> upload(UploadClient uploadClient, ZipEntry zipEntry, InputStream inputStream, String path) throws IOException, URISyntaxException;
}
