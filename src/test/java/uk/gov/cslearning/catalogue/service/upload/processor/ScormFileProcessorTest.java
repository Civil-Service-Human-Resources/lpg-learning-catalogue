package uk.gov.cslearning.catalogue.service.upload.processor;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.multipart.MultipartFile;
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
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(IOUtils.class)
public class ScormFileProcessorTest {
    @Mock
    private InputStreamFactory inputStreamFactory;

    @Mock
    private Map<String, String> manifestXpaths;

    @Mock
    private DocumentBuilderFactory documentBuilderFactory;

    @Mock
    private XPathFactory xPathFactory;

    @Mock
    private ProcessedFileFactory processedFileFactory;

    @InjectMocks
    private ScormFileProcessor processor;

    @Test
    public void shouldEvaluateXPath() throws IOException, SAXException, XPathExpressionException, ParserConfigurationException {
        MultipartFile file = mock(MultipartFile.class);
        FileUpload fileUpload = mock(FileUpload.class);
        InputStream inputStream = mock(InputStream.class);
        ZipInputStream zipInputStream = mock(ZipInputStream.class);

        when(fileUpload.getFile()).thenReturn(file);
        when(file.getInputStream()).thenReturn(inputStream);
        when(inputStreamFactory.createZipInputStream(inputStream)).thenReturn(zipInputStream);

        ZipEntry zipEntry = mock(ZipEntry.class);
        String name = "test.txt";
        when(zipEntry.getName()).thenReturn(name);

        when(zipInputStream.getNextEntry()).thenReturn(zipEntry).thenReturn(null);

        when(manifestXpaths.containsKey(name)).thenReturn(true);

        mockStatic(IOUtils.class);

        byte[] bytes = "Hello World!".getBytes();
        when(IOUtils.toByteArray(zipInputStream)).thenReturn(bytes);

        ByteArrayInputStream byteArrayInputStream = mock(ByteArrayInputStream.class);
        when(inputStreamFactory.createByteArrayInputStream(bytes)).thenReturn(byteArrayInputStream);

        DocumentBuilder documentBuilder = mock(DocumentBuilder.class);
        when(documentBuilderFactory.newDocumentBuilder()).thenReturn(documentBuilder);
        Document document = mock(Document.class);
        when(documentBuilder.parse(byteArrayInputStream)).thenReturn(document);

        XPath xPath = mock(XPath.class);
        when(xPathFactory.newXPath()).thenReturn(xPath);

        XPathExpression xPathExpression = mock(XPathExpression.class);

        String xpathString = ".";

        when(manifestXpaths.get(name)).thenReturn(xpathString);

        when(xPath.compile(xpathString)).thenReturn(xPathExpression);

        String nodeContent = "start-page";
        when(xPathExpression.evaluate(document, XPathConstants.STRING)).thenReturn(nodeContent);

        Map<String, String> metadata = ImmutableMap.of("startPage", nodeContent);

        ProcessedFile processedFile = mock(ProcessedFile.class);
        when(processedFileFactory.create(eq(fileUpload), eq(metadata))).thenReturn(processedFile);

        assertEquals(processedFile, processor.process(fileUpload));
    }

    @Test
    public void shouldIgnoreNonManifestFiles() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        FileUpload fileUpload = mock(FileUpload.class);
        InputStream inputStream = mock(InputStream.class);
        ZipInputStream zipInputStream = mock(ZipInputStream.class);

        when(fileUpload.getFile()).thenReturn(file);
        when(file.getInputStream()).thenReturn(inputStream);
        when(inputStreamFactory.createZipInputStream(inputStream)).thenReturn(zipInputStream);

        ZipEntry zipEntry = mock(ZipEntry.class);
        String name = "test.txt";
        when(zipEntry.getName()).thenReturn(name);

        when(zipInputStream.getNextEntry()).thenReturn(zipEntry).thenReturn(null);

        when(manifestXpaths.containsKey(name)).thenReturn(false);

        Map<String, String> metadata = Collections.emptyMap();

        ProcessedFile processedFile = mock(ProcessedFile.class);
        when(processedFileFactory.create(eq(fileUpload), eq(metadata))).thenReturn(processedFile);

        assertEquals(processedFile, processor.process(fileUpload));

        verifyZeroInteractions(documentBuilderFactory, xPathFactory);
    }

    @Test
    public void shouldCatchIOException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        FileUpload fileUpload = mock(FileUpload.class);

        when(fileUpload.getFile()).thenReturn(file);
        IOException exception = mock(IOException.class);

        doThrow(exception).when(file).getInputStream();

        try {
            processor.process(fileUpload);
            fail("Expected FileUploadException");
        } catch (FileUploadException e) {
            assertEquals(exception, e.getCause());
        }
    }


    @Test
    public void shouldCatchParserConfigurationException() throws ParserConfigurationException, IOException {
        MultipartFile file = mock(MultipartFile.class);
        FileUpload fileUpload = mock(FileUpload.class);
        InputStream inputStream = mock(InputStream.class);
        ZipInputStream zipInputStream = mock(ZipInputStream.class);

        when(fileUpload.getFile()).thenReturn(file);
        when(file.getInputStream()).thenReturn(inputStream);
        when(inputStreamFactory.createZipInputStream(inputStream)).thenReturn(zipInputStream);

        ZipEntry zipEntry = mock(ZipEntry.class);
        String name = "test.txt";
        when(zipEntry.getName()).thenReturn(name);

        when(zipInputStream.getNextEntry()).thenReturn(zipEntry).thenReturn(null);

        when(manifestXpaths.containsKey(name)).thenReturn(true);

        ParserConfigurationException exception = mock(ParserConfigurationException.class);

        doThrow(exception).when(documentBuilderFactory).newDocumentBuilder();

        try {
            processor.process(fileUpload);
            fail("Expected FileUploadException");
        } catch (FileUploadException e) {
            assertEquals(exception, e.getCause());
        }
    }

    @Test
    public void shouldCatchSaxException() throws IOException, SAXException, ParserConfigurationException {
        MultipartFile file = mock(MultipartFile.class);
        FileUpload fileUpload = mock(FileUpload.class);
        InputStream inputStream = mock(InputStream.class);
        ZipInputStream zipInputStream = mock(ZipInputStream.class);

        when(fileUpload.getFile()).thenReturn(file);
        when(file.getInputStream()).thenReturn(inputStream);
        when(inputStreamFactory.createZipInputStream(inputStream)).thenReturn(zipInputStream);

        ZipEntry zipEntry = mock(ZipEntry.class);
        String name = "test.txt";
        when(zipEntry.getName()).thenReturn(name);

        when(zipInputStream.getNextEntry()).thenReturn(zipEntry).thenReturn(null);

        when(manifestXpaths.containsKey(name)).thenReturn(true);
        DocumentBuilder documentBuilder = mock(DocumentBuilder.class);
        when(documentBuilderFactory.newDocumentBuilder()).thenReturn(documentBuilder);

        mockStatic(IOUtils.class);

        byte[] bytes = "Hello World!".getBytes();
        when(IOUtils.toByteArray(zipInputStream)).thenReturn(bytes);

        ByteArrayInputStream byteArrayInputStream = mock(ByteArrayInputStream.class);
        when(inputStreamFactory.createByteArrayInputStream(bytes)).thenReturn(byteArrayInputStream);

        SAXException exception = mock(SAXException.class);
        doThrow(exception).when(documentBuilder).parse(byteArrayInputStream);

        try {
            processor.process(fileUpload);
            fail("Expected FileUploadException");
        } catch (FileUploadException e) {
            assertEquals(exception, e.getCause());
        }
    }

    @Test
    public void shouldCatchXPathExpressionException() throws XPathExpressionException, IOException, SAXException, ParserConfigurationException {
        MultipartFile file = mock(MultipartFile.class);
        FileUpload fileUpload = mock(FileUpload.class);
        InputStream inputStream = mock(InputStream.class);
        ZipInputStream zipInputStream = mock(ZipInputStream.class);

        when(fileUpload.getFile()).thenReturn(file);
        when(file.getInputStream()).thenReturn(inputStream);
        when(inputStreamFactory.createZipInputStream(inputStream)).thenReturn(zipInputStream);

        ZipEntry zipEntry = mock(ZipEntry.class);
        String name = "test.txt";
        when(zipEntry.getName()).thenReturn(name);

        when(zipInputStream.getNextEntry()).thenReturn(zipEntry).thenReturn(null);

        when(manifestXpaths.containsKey(name)).thenReturn(true);
        DocumentBuilder documentBuilder = mock(DocumentBuilder.class);
        when(documentBuilderFactory.newDocumentBuilder()).thenReturn(documentBuilder);
        Document document = mock(Document.class);

        mockStatic(IOUtils.class);

        byte[] bytes = "Hello World!".getBytes();
        when(IOUtils.toByteArray(zipInputStream)).thenReturn(bytes);

        ByteArrayInputStream byteArrayInputStream = mock(ByteArrayInputStream.class);
        when(inputStreamFactory.createByteArrayInputStream(bytes)).thenReturn(byteArrayInputStream);

        when(documentBuilder.parse(byteArrayInputStream)).thenReturn(document);

        XPath xPath = mock(XPath.class);
        when(xPathFactory.newXPath()).thenReturn(xPath);

        XPathExpression xPathExpression = mock(XPathExpression.class);

        String xpathString = ".";

        when(manifestXpaths.get(name)).thenReturn(xpathString);

        when(xPath.compile(xpathString)).thenReturn(xPathExpression);

        XPathExpressionException exception = mock(XPathExpressionException.class);
        doThrow(exception).when(xPathExpression).evaluate(document, XPathConstants.STRING);

        try {
            processor.process(fileUpload);
            fail("Expected FileUploadException");
        } catch (FileUploadException e) {
            assertEquals(exception, e.getCause());
        }
    }
}