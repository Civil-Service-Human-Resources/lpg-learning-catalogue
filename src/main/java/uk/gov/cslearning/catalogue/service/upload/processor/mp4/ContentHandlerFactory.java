package uk.gov.cslearning.catalogue.service.upload.processor.mp4;

import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;
import org.xml.sax.ContentHandler;

@Component
public class ContentHandlerFactory {
    public ContentHandler createBodyContentHandler() {
        return new BodyContentHandler();
    }
}
