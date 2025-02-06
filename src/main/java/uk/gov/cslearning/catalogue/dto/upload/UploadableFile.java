package uk.gov.cslearning.catalogue.dto.upload;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class UploadableFile {

    final String name;
    final String destination;
    final long fileSize;
    final InputStream inputStream;
    final String contentType;
    Map<String, Object> metadata = Collections.emptyMap();

    public String getFullPath() {
        return String.join("/", destination, name);
    }


}
