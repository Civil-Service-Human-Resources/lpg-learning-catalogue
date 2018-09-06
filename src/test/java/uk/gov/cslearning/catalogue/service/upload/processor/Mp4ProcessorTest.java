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
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.ProcessedFileFactory;
import uk.gov.cslearning.catalogue.exception.FileUploadException;
import uk.gov.cslearning.catalogue.service.upload.processor.mp4.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Map;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

@RunWith(MockitoJUnitRunner.class)
public class Mp4ProcessorTest {
    @Mock
    private ProcessedFileFactory processedFileFactory;

    @Mock
    private ParserFactory parserFactory;

    @Mock
    private ContentHandlerFactory contentHandlerFactory;

    @Mock
    private MetadataFactory metadataFactory;

    @Mock
    private ParseContextFactory parseContextFactory;

    @InjectMocks
    private Mp4Processor mp4Processor;

    @Test
    public void processShouldSetMetadata() throws IOException, TikaException, SAXException {

        Duration duration = Duration.ofMillis(190500);
        long imageWidth = 640;
        long imageHeight = 360;

        Map<String, Object> metadataMap = ImmutableMap.of("duration", duration,
                "imageWidth", imageWidth, "imageHeight", imageHeight);

        Parser parser = mock(Parser.class);
        when(parserFactory.createMp4Parser()).thenReturn(parser);
        ContentHandler contentHandler = mock(ContentHandler.class);
        when(contentHandlerFactory.createBodyContentHandler()).thenReturn(contentHandler);

        Metadata metadata = mock(Metadata.class);
        when(metadataFactory.create()).thenReturn(metadata);

        ParseContext parseContext = mock(ParseContext.class);
        when(parseContextFactory.create()).thenReturn(parseContext);

        MultipartFile multipartFile = mock(MultipartFile.class);
        InputStream inputStream = mock(InputStream.class);
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        FileUpload fileUpload = mock(FileUpload.class);
        when(fileUpload.getFile()).thenReturn(multipartFile);

        doAnswer(
                invocation -> {
                    when(metadata.get(Mp4MetadataKeys.DURATION.toString())).thenReturn("190.5");
                    when(metadata.get(Mp4MetadataKeys.IMAGE_WIDTH.toString())).thenReturn(String.valueOf(imageWidth));
                    when(metadata.get(Mp4MetadataKeys.IMAGE_HEIGHT.toString())).thenReturn(String.valueOf(imageHeight));
                    return null;
        }).when(parser).parse(inputStream, contentHandler, metadata, parseContext);

        ProcessedFile processedFile = mock(ProcessedFile.class);
        when(processedFileFactory.create(eq(fileUpload), argThat(allOf(hasEntry("duration", (Object) duration),
                hasEntry("imageWidth", (Object) imageWidth), hasEntry("imageHeight", (Object) imageHeight))))).thenReturn(processedFile);

        ProcessedFile result = mp4Processor.process(fileUpload);

        assertEquals(processedFile, result);

        verify(parser).parse(inputStream, contentHandler, metadata, parseContext);
    }


    @Test
    public void shouldThrowFileUploadExceptionOnIOException() throws IOException {
        Parser parser = mock(Parser.class);
        when(parserFactory.createMp4Parser()).thenReturn(parser);
        ContentHandler contentHandler = mock(ContentHandler.class);
        when(contentHandlerFactory.createBodyContentHandler()).thenReturn(contentHandler);

        Metadata metadata = mock(Metadata.class);
        when(metadataFactory.create()).thenReturn(metadata);

        ParseContext parseContext = mock(ParseContext.class);
        when(parseContextFactory.create()).thenReturn(parseContext);

        MultipartFile multipartFile = mock(MultipartFile.class);

        IOException ioException = mock(IOException.class);
        doThrow(ioException).when(multipartFile).getInputStream();
        FileUpload fileUpload = mock(FileUpload.class);
        when(fileUpload.getFile()).thenReturn(multipartFile);

        try {
            mp4Processor.process(fileUpload);
            fail("Expected FileUploadException");
        } catch (FileUploadException e) {
            assertEquals(ioException, e.getCause());
        }
    }

    @Test
    public void shouldThrowFileUploadExceptionOnTikaException() throws IOException, TikaException, SAXException {
        Parser parser = mock(Parser.class);
        when(parserFactory.createMp4Parser()).thenReturn(parser);
        ContentHandler contentHandler = mock(ContentHandler.class);
        when(contentHandlerFactory.createBodyContentHandler()).thenReturn(contentHandler);

        Metadata metadata = mock(Metadata.class);
        when(metadataFactory.create()).thenReturn(metadata);

        ParseContext parseContext = mock(ParseContext.class);
        when(parseContextFactory.create()).thenReturn(parseContext);

        MultipartFile multipartFile = mock(MultipartFile.class);
        InputStream inputStream = mock(InputStream.class);
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        FileUpload fileUpload = mock(FileUpload.class);
        when(fileUpload.getFile()).thenReturn(multipartFile);

        TikaException tikaException = mock(TikaException.class);

        doThrow(tikaException).when(parser).parse(inputStream, contentHandler, metadata, parseContext);

        try {
            mp4Processor.process(fileUpload);
            fail("Expected FileUploadException");
        } catch (FileUploadException e) {
            assertEquals(tikaException, e.getCause());
        }
    }

    @Test
    public void shouldThrowFileUploadExceptionOnSAXException() throws IOException, TikaException, SAXException {
        Parser parser = mock(Parser.class);
        when(parserFactory.createMp4Parser()).thenReturn(parser);
        ContentHandler contentHandler = mock(ContentHandler.class);
        when(contentHandlerFactory.createBodyContentHandler()).thenReturn(contentHandler);

        Metadata metadata = mock(Metadata.class);
        when(metadataFactory.create()).thenReturn(metadata);

        ParseContext parseContext = mock(ParseContext.class);
        when(parseContextFactory.create()).thenReturn(parseContext);

        MultipartFile multipartFile = mock(MultipartFile.class);
        InputStream inputStream = mock(InputStream.class);
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        FileUpload fileUpload = mock(FileUpload.class);
        when(fileUpload.getFile()).thenReturn(multipartFile);

        SAXException saxException = mock(SAXException.class);

        doThrow(saxException).when(parser).parse(inputStream, contentHandler, metadata, parseContext);

        try {
            mp4Processor.process(fileUpload);
            fail("Expected FileUploadException");
        } catch (FileUploadException e) {
            assertEquals(saxException, e.getCause());
        }
    }
}