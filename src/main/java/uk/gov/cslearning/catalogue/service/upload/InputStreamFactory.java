package uk.gov.cslearning.catalogue.service.upload;

import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

@Component
public class InputStreamFactory {

    public ZipInputStream createZipInputStream(InputStream inputStream) {
        return new ZipInputStream(inputStream);
    }

    public InputStream createByteArrayInputStream(byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }
}
