package uk.gov.cslearning.catalogue.service.upload.processor;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.ProcessedFileFactory;
import uk.gov.cslearning.catalogue.exception.FileUploadException;
import uk.gov.cslearning.catalogue.service.upload.InputStreamFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class ScormFileProcessor implements FileProcessor {
    private final InputStreamFactory inputStreamFactory;
    private final Map<String, String> manifestXpaths;
    private final DocumentBuilderFactory documentBuilderFactory;
    private final XPathFactory xPathFactory;
    private final ProcessedFileFactory processedFileFactory;

    public ScormFileProcessor(InputStreamFactory inputStreamFactory, @Qualifier("scormManifestXpathMap") Map<String, String> manifestXpaths, DocumentBuilderFactory documentBuilderFactory, XPathFactory xPathFactory, ProcessedFileFactory processedFileFactory) {
        this.inputStreamFactory = inputStreamFactory;
        this.manifestXpaths = manifestXpaths;
        this.documentBuilderFactory = documentBuilderFactory;
        this.xPathFactory = xPathFactory;
        this.processedFileFactory = processedFileFactory;
    }

    @Override
    public ProcessedFile process(FileUpload fileUpload) {
        try (ZipInputStream inputStream = inputStreamFactory.createZipInputStream(fileUpload.getFile().getInputStream())){
            Map<String, String> metadata = Collections.emptyMap();

            ZipEntry zipEntry = inputStream.getNextEntry();

            while (zipEntry != null) {
                if (manifestXpaths.containsKey(zipEntry.getName())) {
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                    byte[] bytes = IOUtils.toByteArray(inputStream);
                    Document document = documentBuilder.parse(inputStreamFactory.createByteArrayInputStream(bytes));
                    XPath xPath = xPathFactory.newXPath();

                    String startPage = (String) xPath.compile(manifestXpaths.get(zipEntry.getName())).evaluate(document, XPathConstants.STRING);

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
