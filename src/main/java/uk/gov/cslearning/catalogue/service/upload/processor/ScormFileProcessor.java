package uk.gov.cslearning.catalogue.service.upload.processor;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.ProcessedFileFactory;
import uk.gov.cslearning.catalogue.exception.FileUploadException;
import uk.gov.cslearning.catalogue.service.upload.InputStreamFactory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class ScormFileProcessor implements FileProcessor {
    private final InputStreamFactory inputStreamFactory;
    private final Map<String, String> manifestXpaths;
    private final XPathProcessor xPathProcessor;
    private final ProcessedFileFactory processedFileFactory;

    public ScormFileProcessor(InputStreamFactory inputStreamFactory, @Qualifier("scormManifestXpathMap") Map<String, String> manifestXpaths, XPathProcessor xPathProcessor, ProcessedFileFactory processedFileFactory) {
        this.inputStreamFactory = inputStreamFactory;
        this.manifestXpaths = manifestXpaths;
        this.xPathProcessor = xPathProcessor;
        this.processedFileFactory = processedFileFactory;
    }

    @Override
    public ProcessedFile process(FileUpload fileUpload) {
        try (ZipInputStream inputStream = inputStreamFactory.createZipInputStream(fileUpload.getFile().getInputStream())){
            Map<String, String> metadata = Collections.emptyMap();

            ZipEntry zipEntry = inputStream.getNextEntry();

            while (zipEntry != null) {
                if (manifestXpaths.containsKey(zipEntry.getName())) {
                    String startPage = xPathProcessor.evaluate(manifestXpaths.get(zipEntry.getName()), inputStream);
                    metadata = ImmutableMap.of("startPage", startPage);
                }
                zipEntry = inputStream.getNextEntry();
            }
            return processedFileFactory.create(fileUpload, metadata);
        } catch (IOException | ParserConfigurationException | SAXException | XPathExpressionException e) {
            throw new FileUploadException(e);
        }
    }
}
