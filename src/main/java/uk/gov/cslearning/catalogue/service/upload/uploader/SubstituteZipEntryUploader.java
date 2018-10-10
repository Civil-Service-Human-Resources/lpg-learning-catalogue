package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.UploadedFile;
import uk.gov.cslearning.catalogue.service.upload.FileFactory;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;
import uk.gov.cslearning.catalogue.service.upload.processor.MetadataParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;

@Component
public class SubstituteZipEntryUploader implements ZipEntryUploader {
    private final FileFactory fileFactory;
    private final Map<String, String> fileSubstitutions;
    private final MetadataParser metadataParser;

    public SubstituteZipEntryUploader(FileFactory fileFactory,
                                      @Qualifier("fileSubstitutions") Map<String, String> fileSubstitutions,
                                      MetadataParser metadataParser) {
        this.fileFactory = fileFactory;
        this.fileSubstitutions = fileSubstitutions;
        this.metadataParser = metadataParser;
    }

    @Override
    public Optional<UploadedFile> upload(UploadClient uploadClient, ZipEntry zipEntry, InputStream inputStream,
                                         String path) throws IOException {

        try (InputStream fileInputStream = fileFactory.getInputStreamFromPath(fileSubstitutions.get(zipEntry.getName()))) {
            String contentType = metadataParser.getContentType(fileInputStream, zipEntry.getName());
            return Optional.of(uploadClient.upload(fileInputStream, path, fileInputStream.available(), contentType));
        }
    }

}
