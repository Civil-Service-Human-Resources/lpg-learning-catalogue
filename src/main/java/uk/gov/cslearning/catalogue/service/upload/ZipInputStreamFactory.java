package uk.gov.cslearning.catalogue.service.upload;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

@Component
public class ZipInputStreamFactory {

    public ZipInputStream create(InputStream inputStream) {
        return new ZipInputStream(inputStream);
    }
}
