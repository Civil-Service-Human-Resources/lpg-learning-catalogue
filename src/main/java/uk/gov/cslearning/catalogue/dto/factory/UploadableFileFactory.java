package uk.gov.cslearning.catalogue.dto.factory;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.dto.UploadableFile;
import uk.gov.cslearning.catalogue.service.upload.processor.MetadataParser;

import java.io.IOException;
import java.io.InputStream;

@Service
public class UploadableFileFactory {

    private final MetadataParser metadataParser;

    public UploadableFileFactory(MetadataParser metadataParser) {
        this.metadataParser = metadataParser;
    }

    public UploadableFile create(String filename, InputStream inputStream) throws IOException {
        UploadableFile uploadableFile = new UploadableFile(filename, IOUtils.toByteArray(inputStream));
        String contentType = metadataParser.getContentType(uploadableFile.getAsByteArrayInputStream(), uploadableFile.getName());
        uploadableFile.setContentType(contentType);
        return uploadableFile;
    }
}
