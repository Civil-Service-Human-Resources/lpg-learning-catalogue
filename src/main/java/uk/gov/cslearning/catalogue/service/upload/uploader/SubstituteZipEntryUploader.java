package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.UploadedFile;
import uk.gov.cslearning.catalogue.service.upload.FileFactory;
import uk.gov.cslearning.catalogue.service.upload.InputStreamFactory;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;

@Component
public class SubstituteZipEntryUploader implements ZipEntryUploader {
    private final FileFactory fileFactory;
    private final InputStreamFactory inputStreamFactory;
    private final Map<String, String> fileSubstitutions;

    public SubstituteZipEntryUploader(FileFactory fileFactory,
                                      InputStreamFactory inputStreamFactory,
                                      @Qualifier("fileSubstitutions") Map<String, String> fileSubstitutions) {
        this.fileFactory = fileFactory;
        this.inputStreamFactory = inputStreamFactory;
        this.fileSubstitutions = fileSubstitutions;
    }

    @Override
    public Optional<UploadedFile> upload(UploadClient uploadClient, ZipEntry zipEntry, InputStream inputStream,
                                         String path) throws IOException, URISyntaxException {
        File file = fileFactory.get(fileSubstitutions.get(zipEntry.getName()));

        try (InputStream fileInputStream = inputStreamFactory.createFileInputStream(file)) {
            return Optional.of(uploadClient.upload(fileInputStream, path, file.length()));
        }
    }

}
