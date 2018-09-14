package uk.gov.cslearning.catalogue.service.upload.processor;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.springframework.stereotype.Component;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import uk.gov.cslearning.catalogue.exception.FileUploadException;
import uk.gov.cslearning.catalogue.service.upload.processor.mp4.ContentHandlerFactory;
import uk.gov.cslearning.catalogue.service.upload.processor.mp4.MetadataFactory;
import uk.gov.cslearning.catalogue.service.upload.processor.mp4.ParseContextFactory;
import uk.gov.cslearning.catalogue.service.upload.processor.mp4.ParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class MetadataParser {
    private final ParserFactory parserFactory;
    private final ContentHandlerFactory contentHandlerFactory;
    private final MetadataFactory metadataFactory;
    private final ParseContextFactory parseContextFactory;

    public MetadataParser(ParserFactory parserFactory, ContentHandlerFactory contentHandlerFactory, MetadataFactory metadataFactory, ParseContextFactory parseContextFactory) {
        this.parserFactory = parserFactory;
        this.contentHandlerFactory = contentHandlerFactory;
        this.metadataFactory = metadataFactory;
        this.parseContextFactory = parseContextFactory;
    }

    public Map<String, String> parse(InputStream inputStream) {
        Parser parser = parserFactory.create();
        ContentHandler handler = contentHandlerFactory.createBodyContentHandler();
        Metadata metadata = metadataFactory.create();
        ParseContext parseContext = parseContextFactory.create();

        try {
            parser.parse(inputStream, handler, metadata, parseContext);

            Map<String, String> data = new HashMap<>();
            Arrays.stream(metadata.names()).forEach(k -> data.put(k, metadata.get(k)));

            return data;
        } catch (IOException | SAXException | TikaException e) {
            throw new FileUploadException(e);
        }
    }
}
