package uk.gov.cslearning.catalogue.service.upload.processor;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import uk.gov.cslearning.catalogue.service.upload.InputStreamFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(IOUtils.class)
public class XPathProcessorTest {

    @Mock
    private InputStreamFactory inputStreamFactory;

    @Mock
    private DocumentBuilderFactory documentBuilderFactory;

    @Mock
    private XPathFactory xPathFactory;

    @InjectMocks
    private XPathProcessor xPathProcessor;

    @Test
    public void shouldEvaluateXPath() throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        String xPathString = ".";

        InputStream inputStream = mock(InputStream.class);


        byte[] bytes = "Hello World!".getBytes();

        mockStatic(IOUtils.class);
        when(IOUtils.toByteArray(inputStream)).thenReturn(bytes);

        ByteArrayInputStream byteArrayInputStream = mock(ByteArrayInputStream.class);
        when(inputStreamFactory.createByteArrayInputStream(bytes)).thenReturn(byteArrayInputStream);

        DocumentBuilder documentBuilder = mock(DocumentBuilder.class);
        when(documentBuilderFactory.newDocumentBuilder()).thenReturn(documentBuilder);
        Document document = mock(Document.class);
        when(documentBuilder.parse(byteArrayInputStream)).thenReturn(document);

        XPath xPath = mock(XPath.class);
        when(xPathFactory.newXPath()).thenReturn(xPath);

        XPathExpression xPathExpression = mock(XPathExpression.class);

        when(xPath.compile(xPathString)).thenReturn(xPathExpression);

        String nodeContent = "start-page";
        when(xPathExpression.evaluate(document, XPathConstants.STRING)).thenReturn(nodeContent);

        assertEquals(nodeContent, xPathProcessor.evaluate(xPathString, inputStream));
    }
}