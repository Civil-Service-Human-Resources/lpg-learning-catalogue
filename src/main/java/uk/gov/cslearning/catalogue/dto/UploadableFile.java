package uk.gov.cslearning.catalogue.dto;

import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Data
public class UploadableFile {

    String name;
    byte[] bytes;
    String contentType;

    public UploadableFile(String name, byte[] bytes) {
        this.name = name;
        this.bytes = bytes;
    }

    public InputStream getAsByteArrayInputStream() {
        return new ByteArrayInputStream(bytes);
    }

}
