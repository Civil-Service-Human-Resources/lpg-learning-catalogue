package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
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

public class ScormManager {
    private static final Logger LOG = LoggerFactory.getLogger(ScormManager.class);

    private final FileFactory fileFactory;
    private final InputStreamFactory inputStreamFactory;
    private final Map<String, String> fileSubstitutions;
    private final UploadClient uploadClient;

    public ScormManager(FileFactory fileFactory, InputStreamFactory inputStreamFactory, UploadClient uploadClient, @Qualifier("fileSubstitutions") Map<String, String> fileSubstitutions) {
        this.fileFactory = fileFactory;
        this.inputStreamFactory = inputStreamFactory;
        this.uploadClient = uploadClient;
        this.fileSubstitutions = fileSubstitutions;
    }

    public boolean isReplacementCandidate(String path) {
        return fileSubstitutions.containsKey(path);
    }

    public Optional<UploadedFile> ignoreOrReplace(String zipPath, String destinationPath) throws URISyntaxException, IOException {
        if (fileSubstitutions.get(zipPath).isEmpty()) {
            return ignore(zipPath);
        }

        return replace(zipPath, destinationPath);
    }

    private Optional<UploadedFile> replace(String zipPath, String destinationPath) throws IOException, URISyntaxException {
        File file = fileFactory.get(fileSubstitutions.get(zipPath));
        try (InputStream fileInputStream = inputStreamFactory.createFileInputStream(file)) {
            return Optional.of(uploadClient.upload(fileInputStream, destinationPath, file.length()));
        }
    }

    private Optional<UploadedFile> ignore(String zipPath) {
        LOG.debug(String.format("%s has empy replacement. Ignoring", zipPath));
        // don't upload anything
        return Optional.empty();
    }
}
