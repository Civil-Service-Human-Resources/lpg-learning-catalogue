package uk.gov.cslearning.catalogue.service.upload;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.dto.upload.FileUpload;
import uk.gov.cslearning.catalogue.dto.upload.UploadableFile;
import uk.gov.cslearning.catalogue.service.upload.processor.metadata.MetadataParser;

import java.io.ByteArrayInputStream;
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
        byte[] bytes = IOUtils.toByteArray(inputStream);
        InputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        int size = bytes.length;
        String contentType = metadataParser.getContentType(byteArrayInputStream, filename);
        return new UploadableFile(filename, destination, size, byteArrayInputStream, contentType);
    }

    public List<UploadableFile> createFromZip(FileUpload fileUpload) throws IOException {
        List<UploadableFile> uploadableFiles = new ArrayList<>();
        try (ZipInputStream inputStream = new ZipInputStream(fileUpload.getFile().getInputStream())) {
            System.out.println("createFromZip: Got input stream");
            ZipEntry zipEntry = inputStream.getNextEntry();
            System.out.println("createFromZip: Got next zip entry.");
            while (zipEntry != null) {
                if (!zipEntry.isDirectory()) {
                    String filename = zipEntry.getName();
                    UploadableFile uploadableFile = createFromZipEntry(filename, fileUpload.getDestination(), inputStream);
                    uploadableFiles.add(uploadableFile);
                }
                zipEntry = inputStream.getNextEntry();
            }
        }
        System.out.println("createFromZip: completed.");
        return uploadableFiles;
    }

    public UploadableFile createFromFileUpload(FileUpload fileUpload) throws IOException {
        InputStream inputStream = fileUpload.getFile().getInputStream();
        return new UploadableFile(fileUpload.getName(),
                fileUpload.getDestination(),
                fileUpload.getFile().getSize(),
                inputStream,
                fileUpload.getFile().getContentType());
    }

}
