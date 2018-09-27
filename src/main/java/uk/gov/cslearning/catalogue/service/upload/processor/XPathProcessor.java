package uk.gov.cslearning.catalogue.service.upload.processor;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import uk.gov.cslearning.catalogue.service.upload.InputStreamFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;

@Component
public class XPathProcessor {
    private final InputStreamFactory inputStreamFactory;
    private final DocumentBuilderFactory documentBuilderFactory;
    private final XPathFactory xPathFactory;

    public XPathProcessor(InputStreamFactory inputStreamFactory, DocumentBuilderFactory documentBuilderFactory, XPathFactory xPathFactory) {
        this.inputStreamFactory = inputStreamFactory;
        this.documentBuilderFactory = documentBuilderFactory;
        this.xPathFactory = xPathFactory;
    }

    String evaluate(String xPathExpression, InputStream inputStream) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        byte[] bytes = IOUtils.toByteArray(inputStream);
        Document document = documentBuilder.parse(inputStreamFactory.createByteArrayInputStream(bytes));
        XPath xPath = xPathFactory.newXPath();

        return (String) xPath.compile(xPathExpression).evaluate(document, XPathConstants.STRING);
    }
}
