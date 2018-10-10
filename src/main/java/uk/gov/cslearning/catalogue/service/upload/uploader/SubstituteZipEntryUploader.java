package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.UploadedFile;
import uk.gov.cslearning.catalogue.service.upload.InputStreamFactory;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;
import uk.gov.cslearning.catalogue.service.upload.processor.MetadataParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;

@Component
public class SubstituteZipEntryUploader implements ZipEntryUploader {
    private final InputStreamFactory inputStreamFactory;
    private final Map<String, String> fileSubstitutions;
    private final MetadataParser metadataParser;

    public SubstituteZipEntryUploader(InputStreamFactory inputStreamFactory,
                                      @Qualifier("fileSubstitutions") Map<String, String> fileSubstitutions,
                                      MetadataParser metadataParser) {
        this.inputStreamFactory = inputStreamFactory;
        this.fileSubstitutions = fileSubstitutions;
        this.metadataParser = metadataParser;
    }

    @Override
    public Optional<UploadedFile> upload(UploadClient uploadClient, ZipEntry zipEntry, InputStream inputStream,
                                         String path) throws IOException {

        try (InputStream inputStream = inputStreamFactory.getInputStreamFromPath(fileSubstitutions.get(zipEntry.getName()))) {
            String contentType = metadataParser.getContentType(inputStream, zipEntry.getName());
            return Optional.of(uploadClient.upload(inputStream, path, inputStream.available(), contentType));
        }
    }

}
