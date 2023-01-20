package uk.gov.cslearning.catalogue.service.upload;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.dto.upload.UploadableFile;
import uk.gov.cslearning.catalogue.service.upload.processor.MetadataParser;

import java.io.IOException;
import java.io.InputStream;

@Service
public class UploadableFileFactory {

    private final MetadataParser metadataParser;

    public UploadableFileFactory(MetadataParser metadataParser) {
        this.metadataParser = metadataParser;
    }

    public UploadableFile create(String filename, String destination, InputStream inputStream) throws IOException {
        String contentType = metadataParser.getContentType(inputStream, filename);
        byte[] bytes = IOUtils.toByteArray(inputStream);
        return new UploadableFile(filename, destination, inputStream, bytes, contentType);
    }

}
