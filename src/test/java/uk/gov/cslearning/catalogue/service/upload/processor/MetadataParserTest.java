package uk.gov.cslearning.catalogue.service.upload.processor;

import com.google.common.collect.ImmutableMap;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import uk.gov.cslearning.catalogue.exception.FileUploadException;
import uk.gov.cslearning.catalogue.service.upload.processor.mp4.ContentHandlerFactory;
import uk.gov.cslearning.catalogue.service.upload.processor.mp4.MetadataFactory;
import uk.gov.cslearning.catalogue.service.upload.processor.mp4.ParseContextFactory;
import uk.gov.cslearning.catalogue.service.upload.processor.mp4.ParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class MetadataParserTest {
    @Mock
    private ParserFactory parserFactory;

    @Mock
    private ContentHandlerFactory contentHandlerFactory;

    @Mock
    private MetadataFactory metadataFactory;

    @Mock
    private ParseContextFactory parseContextFactory;


    @InjectMocks
    private MetadataParser metadataParser;

    @Test
    public void parseShouldReturnMetadata() throws IOException, TikaException, SAXException {
        Parser parser = mock(Parser.class);
        when(parserFactory.create()).thenReturn(parser);
        ContentHandler contentHandler = mock(ContentHandler.class);
        when(contentHandlerFactory.createBodyContentHandler()).thenReturn(contentHandler);

        Metadata metadata = mock(Metadata.class);
        when(metadataFactory.create()).thenReturn(metadata);

        ParseContext parseContext = mock(ParseContext.class);
        when(parseContextFactory.create()).thenReturn(parseContext);

        InputStream inputStream = mock(InputStream.class);

        doAnswer(
                invocation -> {
                    when(metadata.names()).thenReturn(new String[]{"key1", "key2"});
                    when(metadata.get("key1")).thenReturn("value1");
                    when(metadata.get("key2")).thenReturn("value2");
                    return null;
                }).when(parser).parse(inputStream, contentHandler, metadata, parseContext);

        Map<String, String> expected = ImmutableMap.of(
            "key1", "value1",
            "key2", "value2"
        );

        assertEquals(expected, metadataParser.parse(inputStream));
    }

    @Test
    public void shouldThrowFileUploadExceptionOnIOException() throws IOException, TikaException, SAXException {
        Parser parser = mock(Parser.class);
        when(parserFactory.create()).thenReturn(parser);
        ContentHandler contentHandler = mock(ContentHandler.class);
        when(contentHandlerFactory.createBodyContentHandler()).thenReturn(contentHandler);

        Metadata metadata = mock(Metadata.class);
        when(metadataFactory.create()).thenReturn(metadata);

        ParseContext parseContext = mock(ParseContext.class);
        when(parseContextFactory.create()).thenReturn(parseContext);

        InputStream inputStream = mock(InputStream.class);

        IOException ioException = mock(IOException.class);

        doThrow(ioException).when(parser).parse(inputStream, contentHandler, metadata, parseContext);

        try {
            metadataParser.parse(inputStream);
            fail("Expected FileUploadException");
        } catch (FileUploadException e) {
            assertEquals(ioException, e.getCause());
        }
    }

    @Test
    public void shouldThrowFileUploadExceptionOnTikaException() throws IOException, TikaException, SAXException {
        Parser parser = mock(Parser.class);
        when(parserFactory.create()).thenReturn(parser);
        ContentHandler contentHandler = mock(ContentHandler.class);
        when(contentHandlerFactory.createBodyContentHandler()).thenReturn(contentHandler);

        Metadata metadata = mock(Metadata.class);
        when(metadataFactory.create()).thenReturn(metadata);

        ParseContext parseContext = mock(ParseContext.class);
        when(parseContextFactory.create()).thenReturn(parseContext);

        InputStream inputStream = mock(InputStream.class);

        TikaException tikaException = mock(TikaException.class);

        doThrow(tikaException).when(parser).parse(inputStream, contentHandler, metadata, parseContext);

        try {
            metadataParser.parse(inputStream);
            fail("Expected FileUploadException");
        } catch (FileUploadException e) {
            assertEquals(tikaException, e.getCause());
        }
    }

    @Test
    public void shouldThrowFileUploadExceptionOnSAXException() throws IOException, TikaException, SAXException {
        Parser parser = mock(Parser.class);
        when(parserFactory.create()).thenReturn(parser);
        ContentHandler contentHandler = mock(ContentHandler.class);
        when(contentHandlerFactory.createBodyContentHandler()).thenReturn(contentHandler);

        Metadata metadata = mock(Metadata.class);
        when(metadataFactory.create()).thenReturn(metadata);

        ParseContext parseContext = mock(ParseContext.class);
        when(parseContextFactory.create()).thenReturn(parseContext);

        InputStream inputStream = mock(InputStream.class);

        SAXException saxException = mock(SAXException.class);

        doThrow(saxException).when(parser).parse(inputStream, contentHandler, metadata, parseContext);

        try {
            metadataParser.parse(inputStream);
            fail("Expected FileUploadException");
        } catch (FileUploadException e) {
            assertEquals(saxException, e.getCause());
        }
    }
}