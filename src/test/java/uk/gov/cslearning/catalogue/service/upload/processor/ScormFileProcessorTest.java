package uk.gov.cslearning.catalogue.service.upload.processor;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.ProcessedFileFactory;
import uk.gov.cslearning.catalogue.exception.FileUploadException;
import uk.gov.cslearning.catalogue.service.upload.InputStreamFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
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
@RunWith(PowerMockRunner.class)
public class ScormFileProcessorTest {
    @Mock
    private InputStreamFactory inputStreamFactory;
    @Mock
    private Map<String, String> manifestXpaths;
    @Mock
    private XPathProcessor xPathProcessor;
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
        String xpathString = ".";
        when(manifestXpaths.get(name)).thenReturn(xpathString);
        String nodeContent = "start-page";
        when(xPathProcessor.evaluate(xpathString, zipInputStream)).thenReturn(nodeContent);
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
        verifyZeroInteractions(xPathProcessor);
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
    public void shouldCatchParserConfigurationException() throws ParserConfigurationException, IOException, XPathExpressionException, SAXException {
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
        String xpathString = ".";
        when(manifestXpaths.get(name)).thenReturn(xpathString);
        ParserConfigurationException exception = mock(ParserConfigurationException.class);
        doThrow(exception).when(xPathProcessor).evaluate(xpathString, zipInputStream);
        try {
            processor.process(fileUpload);
            fail("Expected FileUploadException");
        } catch (FileUploadException e) {
            assertEquals(exception, e.getCause());
        }
    }
    @Test
    public void shouldCatchSaxException() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
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
        String xpathString = ".";
        when(manifestXpaths.get(name)).thenReturn(xpathString);
        XPathExpressionException exception = mock(XPathExpressionException.class);
        doThrow(exception).when(xPathProcessor).evaluate(xpathString, zipInputStream);
        try {
            processor.process(fileUpload);
            fail("Expected FileUploadException");
        } catch (FileUploadException e) {
            assertEquals(exception, e.getCause());
        }
    }
}