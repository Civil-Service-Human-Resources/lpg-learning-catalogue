package uk.gov.cslearning.catalogue.service.upload.processor.mp4;

import org.apache.tika.sax.BodyContentHandler;
import org.junit.Test;
import org.xml.sax.ContentHandler;

import static org.junit.Assert.assertTrue;

public class ContentHandlerFactoryTest {
    private final ContentHandlerFactory contentHandlerFactory = new ContentHandlerFactory();

    @Test
    public void shouldReturnBodyContentHandler() {
        ContentHandler contentHandler = contentHandlerFactory.createBodyContentHandler();
        assertTrue(contentHandler instanceof BodyContentHandler);
    }
}