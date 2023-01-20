package uk.gov.cslearning.catalogue.dto.upload;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class UploadableFile {

    final String name;
    final String destination;
    final InputStream inputStream;
    final byte[] bytes;
    final String contentType;
    Map<String, Object> metadata = Collections.emptyMap();

    public static UploadableFile createFromFileUpload(FileUpload fileUpload) throws IOException {
        InputStream inputStream = fileUpload.getFile().getInputStream();
        byte[] bytes = IOUtils.toByteArray(inputStream);
        return new UploadableFile(fileUpload.getName(),
                fileUpload.getDestination(), inputStream, bytes,
                fileUpload.getFile().getContentType());
    }

    public String getFullPath() {
        return String.join("/", destination, name);
    }

    public InputStream getAsByteArrayInputStream() throws IOException {
        return new ByteArrayInputStream(getInputStreamAsBytes());
    }
    public byte[] getInputStreamAsBytes() throws IOException { return IOUtils.toByteArray(inputStream); }

}
