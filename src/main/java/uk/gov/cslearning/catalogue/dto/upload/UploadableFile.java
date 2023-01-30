package uk.gov.cslearning.catalogue.dto.upload;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class UploadableFile {

    final String name;
    final String destination;
    final byte[] bytes;
    final String contentType;
    Map<String, Object> metadata = Collections.emptyMap();

    public String getFullPath() {
        return String.join("/", destination, name);
    }


}
