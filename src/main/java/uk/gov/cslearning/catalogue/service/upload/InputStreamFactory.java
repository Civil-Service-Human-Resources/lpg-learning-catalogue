package uk.gov.cslearning.catalogue.service.upload;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

@Component
public class InputStreamFactory {

    public ZipInputStream createZipInputStream(InputStream inputStream) {
        return new ZipInputStream(inputStream);
    }

    public InputStream createFileInputStream(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }
}