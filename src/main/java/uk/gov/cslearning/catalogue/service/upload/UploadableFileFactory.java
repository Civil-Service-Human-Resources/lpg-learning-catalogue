package uk.gov.cslearning.catalogue.service.upload;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.dto.upload.FileUpload;
import uk.gov.cslearning.catalogue.dto.upload.UploadableFile;
import uk.gov.cslearning.catalogue.service.upload.processor.metadata.MetadataParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
public class UploadableFileFactory {

    private final MetadataParser metadataParser;

    public UploadableFileFactory(MetadataParser metadataParser) {
        this.metadataParser = metadataParser;
    }

    private UploadableFile createFromZipEntry(String filename, String destination, InputStream inputStream) throws IOException {
        String contentType = metadataParser.getContentType(inputStream, filename);
        byte[] bytes = IOUtils.toByteArray(inputStream);
        return new UploadableFile(filename, destination, bytes, contentType);
    }

    public List<UploadableFile> createFromZip(FileUpload fileUpload) throws IOException {
        List<UploadableFile> uploadableFiles = new ArrayList<>();
        try (ZipInputStream inputStream = new ZipInputStream(fileUpload.getFile().getInputStream())) {
            ZipEntry zipEntry = inputStream.getNextEntry();
            while (zipEntry != null) {
                if (!zipEntry.isDirectory()) {
                    String filename = zipEntry.getName();
                    UploadableFile uploadableFile = createFromZipEntry(filename, fileUpload.getDestination(), inputStream);
                    uploadableFiles.add(uploadableFile);
                }
                zipEntry = inputStream.getNextEntry();
            }
        }
        return uploadableFiles;
    }

    public UploadableFile createFromFileUpload(FileUpload fileUpload) throws IOException {
        InputStream inputStream = fileUpload.getFile().getInputStream();
        byte[] bytes = IOUtils.toByteArray(inputStream);
        return new UploadableFile(fileUpload.getName(),
                fileUpload.getDestination(), bytes,
                fileUpload.getFile().getContentType());
    }

}
